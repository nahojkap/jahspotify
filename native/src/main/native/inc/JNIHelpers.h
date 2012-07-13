#include <jni.h>
#include <stdio.h>

#ifndef JNI_HELPERS

#define JNI_HELPERS

jint createNativeString(JNIEnv *env, jstring str, char **nativeStr);

jint setObjectFloatField(JNIEnv * env, jobject obj, const char *name, jfloat value);
jint setObjectIntField(JNIEnv * env, jobject obj, const char *name, jint value);
jint setObjectLongField(JNIEnv * env, jobject obj, const char *name, jlong value);
jint setObjectStringField(JNIEnv * env, jobject obj,const char *name, const char *value);

jint setObjectObjectField(JNIEnv * env, jobject obj,const char *name, char *fieldTypeName, jobject value);

jint setObjectBooleanField(JNIEnv * env, jobject obj, const char *name, jint value);

jint getObjectLongField(JNIEnv * env, jobject obj, const char *name, jlong *value);
jstring getObjectStringField(JNIEnv * env, jobject obj, const char *name);
jint getObjectIntField(JNIEnv * env, jobject obj, const char *name, jint *value);

jobject createInstance(JNIEnv *env, char *className);
jobject createInstanceFromJClass(JNIEnv *env, jclass jClass);

jint invokeNonStaticVoidMethod(JNIEnv *env, jobject instance, const char *methodName, const char *methodSig, void *returnValue, ...);

jint checkException(JNIEnv *env);
int retrieveEnv(JNIEnv* env);
jint detachThread();

#endif