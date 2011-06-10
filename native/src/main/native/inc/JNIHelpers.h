#include <jni.h>
#include <stdio.h>

#ifndef JNI_HELPERS

#define JNI_HELPERS

jint setObjectFloatField(JNIEnv * env, jobject obj, const char *name, jfloat value);
jint setObjectIntField(JNIEnv * env, jobject obj, const char *name, jint value);
jint setObjectLongField(JNIEnv * env, jobject obj, const char *name, jlong value);
jint setObjectStringField(JNIEnv * env, jobject obj,const char *name, const char *value);

jint getObjectLongField(JNIEnv * env, jobject obj, const char *name, jlong *value);
jstring getObjectStringField(JNIEnv * env, jobject obj, const char *name);
jint getObjectIntField(JNIEnv * env, jobject obj, const char *name, jint *value);

jobject createInstance(JNIEnv *env, char *className);

int retrieveEnv(JNIEnv* env, int *alreadyAttachedToThread);

#endif