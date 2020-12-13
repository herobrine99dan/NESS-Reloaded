package com.github.ness.reflect;

import java.lang.reflect.Member;

interface MemberDescription<M extends Member> {

	/**
	 * Whether this description matches a member in its details
	 * 
	 * @param member the member
	 * @return true if matched, false otherwise
	 */
	boolean matches(M member);

	/**
	 * See {@link #withOffset(int)}
	 * 
	 * @return the amount of matching members to initially skip
	 */
	int memberOffset();

	/**
	 * Returns a new member description with the specified member offset. <br>
	 * <br>
	 * The first {@code memberOffset} members matching this description will be skipped.
	 * If zero, the first matching member is returned. Members in subclasses are searched first.
	 * 
	 * @param memberOffset the amount of matching members to initially skip
	 * @return a new member description with the specified offset
	 */
	MemberDescription<M> withOffset(int memberOffset);

	/**
	 * Whether this description is equal to another. Two descriptions are equal if they
	 * would match the same members, and have the same member offset.
	 * 
	 * @param object the other object
	 * @return true if equal, false otherwise
	 */
	@Override
	boolean equals(Object object);

}
