package com.github.ness;

import java.net.URL;
import java.net.URLClassLoader;

public class NessClassLoader extends URLClassLoader {

    public NessClassLoader(URL[] urls, ClassLoader classLoader) {
        super(urls,classLoader);
    }
    
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException{
        return super.findClass(name);
    }
}