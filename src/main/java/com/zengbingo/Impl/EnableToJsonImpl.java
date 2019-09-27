package com.zengbingo.Impl;

import com.zengbingo.annotation.EnableToJson;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtMethod;
import org.apache.ibatis.javassist.CtNewMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;

@Service
public class EnableToJsonImpl{

    @Value("${com.zengbingo.enableToJson.pkg}")
    private String pkg;

    @PostConstruct
    public void init(){
            //你要找到包名前缀
            final String pkgNamePrefix = pkg;
            try {
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                        + ClassUtils.convertClassNameToResourcePath(pkgNamePrefix)
                        + "/**/*.class";
                ResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
                Resource[] source = resourceLoader.getResources(pattern);
                MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourceLoader);
                ClassPool pool = ClassPool.getDefault();
                for (Resource resource : source) {
                    if (resource.isReadable()) {
                        try{
                            MetadataReader reader = readerFactory.getMetadataReader(resource);
                            String className = reader.getClassMetadata().getClassName();
                            CtClass ctClazz = pool.get(className);
                            Object enableToJson = ctClazz.getAnnotation(EnableToJson.class);
                            if (null != enableToJson) {
                                CtMethod method = ctClazz.getDeclaredMethod("toString");
                                CtMethod newMethod = CtNewMethod.copy(method, ctClazz, null);
                                newMethod.setBody("{     java.lang.Class c = Class.forName(\"com.alibaba.fastjson.JSON\");\n" +
                                        "        Class[] mc = {java.lang.Object.class};\n" +
                                        "        Object[] params = {this};\n" +
                                        "        return (String)c.getMethod(\"toJSONString\", mc).invoke(null, params); }");
                                ctClazz.removeMethod(method);
                                ctClazz.addMethod(newMethod);
                                ctClazz.toClass();
                                ctClazz.defrost();
                            }
                        } catch (Throwable t){
                            //do nothing
                        }
                    }
                }
            }catch (Exception e){
                //do nothing
            }
    }
}
