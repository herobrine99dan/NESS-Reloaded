package com.github.ness.reflect;

import java.lang.reflect.Member;

interface MemberDescription<T extends Member> {

	boolean matches(T member);

}
