package com.github.ness;

import java.net.URL;
import java.net.URLClassLoader;

public class NessClassLoader extends URLClassLoader  {

    public NessClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

}
