package com.github.ness.packets;

import java.util.UUID;

import com.github.ness.reflect.FieldInvoker;
import com.github.ness.reflect.MemberDescriptions;
import com.github.ness.reflect.MethodInvoker;
import com.github.ness.reflect.Reflection;

class ReusablePacket implements Packet {

	private final Reflection reflection;

	private Object rawPacket;
	private UUID associatedUniqueId;
	private boolean cancelled;

	private static final Class<?>[] EMPTY_CLASSES = new Class<?>[] {};
	private static final Object[] EMPTY_ARGUMENTS = EMPTY_CLASSES;

	ReusablePacket(Reflection reflection) {
		this.reflection = reflection;
	}

	@Override
	public Object getRawPacket() {
		return rawPacket;
	}

	@Override
	public UUID getAssociatedUniqueId() {
		return associatedUniqueId;
	}

	@Override
	public void cancel() {
		cancelled = true;
	}

	void changeTo(UUID associatedUniqueId, Object rawPacket) {
		this.associatedUniqueId = associatedUniqueId;
		this.rawPacket = rawPacket;
		cancelled = false;
	}

	boolean isCancelled() {
		return cancelled;
	}

	@Override
	public <T> FieldInvoker<T> getField(Class<T> fieldType, String fieldName) {
		return reflection.getField(getClass(), MemberDescriptions.forField(fieldName, fieldType));
	}

	@Override
	public FieldInvoker<?> getField(String fieldName) {
		return reflection.getField(getClass(), MemberDescriptions.forField(fieldName));
	}

	@Override
	public <T> T invokeField(Class<T> fieldType, String fieldName) {
		return getField(fieldType, fieldName).get(this);
	}

	@Override
	public Object invokeField(String fieldName) {
		return getField(fieldName).get(this);
	}

	@Override
	public <R> MethodInvoker<R> getMethod(Class<R> returnType, String methodName) {
		return reflection.getMethod(getClass(), MemberDescriptions.forMethod(returnType, methodName, EMPTY_CLASSES));
	}

	@Override
	public <R> MethodInvoker<R> getMethod(Class<R> returnType, String methodName, Class<?>... parameterTypes) {
		return reflection.getMethod(getClass(), MemberDescriptions.forMethod(returnType, methodName, parameterTypes));
	}

	@Override
	public MethodInvoker<?> getMethod(String methodName) {
		return reflection.getMethod(getClass(), MemberDescriptions.forMethod(methodName, EMPTY_CLASSES));
	}

	@Override
	public MethodInvoker<?> getMethod(String methodName, Class<?>... parameterTypes) {
		return reflection.getMethod(getClass(), MemberDescriptions.forMethod(methodName, parameterTypes));
	}

	@Override
	public <R> R invokeMethod(Class<R> returnType, String methodName) {
		return getMethod(returnType, methodName, EMPTY_CLASSES).invoke(this, EMPTY_ARGUMENTS);
	}

	@Override
	public <R> R invokeMethod(Class<R> returnType, String methodName, Class<?>[] parameterTypes, Object... arguments) {
		return getMethod(returnType, methodName, parameterTypes).invoke(this, arguments);
	}

}
