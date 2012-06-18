#include <string.h>
#include <stdlib.h>
#include <stdarg.h>
#include <libspotify/api.h>

#include "JNIHelpers.h"
#include "Logging.h"

JavaVM* g_vm = NULL;

jclass g_playbackListenerClass;
jclass g_libraryListenerClass;
jclass g_connectionListenerClass;
jclass g_searchCompleteListenerClass;
jclass g_mediaLoadedListenerClass;
jclass g_linkClass;
jclass g_nativeSearchResultClass;
jclass g_loggerClass;

jint checkException(JNIEnv *env)
{
  if ((*env)->ExceptionCheck(env) == JNI_TRUE)
  {
    (*env)->ExceptionDescribe(env);
    // Handle the xception as well
    (*env)->ExceptionClear(env);
    return 1;
  }    
  return 0;
}

jint detachThread()
{
  JNIEnv* env = NULL;
  jint result = (*g_vm)->GetEnv(g_vm,(void**)&env, JNI_VERSION_1_4);
  
  if (result != JNI_EDETACHED)
  {
    result = (*g_vm)->DetachCurrentThread(g_vm);
    if (result != JNI_OK)
    {
      return 1;
    }
  }
  return 0;
}

jint invokeNonStaticVoidMethod(JNIEnv *env, jobject instance, const char *methodName, const char *methodSig, void *returnValue, ...)
{
/*    jclass clazz;
    jmethodID method;

    clazz = (*env)->GetObjectClass(env, obj);
    if (clazz == NULL)
        return 1;
    
    method = (*env).GetMethodID(clazz,methodName, methodSig);

    if (method == NULL)
        return 1;
    
    
    
    
    
    
    
    */
}


jint createNativeString(JNIEnv *env, jstring str, char **nativeStr)
{
  char *tmpStr = NULL;
  char *tmpStrCopy = NULL;
  if (str)
  {
    tmpStr = (char*)(*env)->GetStringUTFChars(env, str, NULL);
    tmpStrCopy = (char*)malloc(strlen(tmpStr)+1);
    strcpy(tmpStrCopy,tmpStr);
    *nativeStr = tmpStrCopy;
    if (tmpStr) (*env)->ReleaseStringUTFChars(env, str, (char *)tmpStr);
  }
  return 0;
}

jobject createInstance(JNIEnv *env, char *className)
{
  jclass jClass = (*env)->FindClass(env, className);
  if (jClass == NULL)
  {
      log_error("jahspotify","createInstance","Could not load class: %s", className);
      return NULL;
  }
  
  return createInstanceFromJClass(env, jClass);
 
}

jobject createInstanceFromJClass(JNIEnv *env, jclass jClass)
{
  jobject instance = NULL;
  jmethodID jConstructor = NULL;
  
  jConstructor = (*env)->GetMethodID(env,jClass, "<init>","()V");
  
  // jobject instance = (*env)->AllocObject(env,jClass);
  instance = (*env)->NewObject(env, jClass,jConstructor);
  
  if (instance == NULL)
  {
      log_error("jahspotify","createInstance","Could not create instance");
      return NULL;
  }
  return instance;
}


jint setObjectIntField(JNIEnv * env, jobject obj, const char *name, jint value)
{
    jclass clazz;
    jfieldID field;

    clazz = (*env)->GetObjectClass(env, obj);
    if (clazz == NULL)
        return 1;

    field = (*env)->GetFieldID(env, clazz, name, "I");
    (*env)->DeleteLocalRef(env, clazz);

    if (field == NULL)
        return 1;

    (*env)->SetIntField(env, obj, field, value);

    return 0;
}

jint setObjectFloatField(JNIEnv * env, jobject obj, const char *name, jfloat value)
{
    jclass clazz;
    jfieldID field;

    clazz = (*env)->GetObjectClass(env, obj);
    if (clazz == NULL)
        return 1;

    field = (*env)->GetFieldID(env, clazz, name, "F");
    (*env)->DeleteLocalRef(env, clazz);

    if (field == NULL)
        return 1;

    (*env)->SetFloatField(env, obj, field, value);

    return 0;
}

jint getObjectLongField(JNIEnv * env, jobject obj, const char *name, jlong *value)
{
    jclass clazz;
    jfieldID field;

    clazz = (*env)->GetObjectClass(env, obj);
    if (clazz == NULL)
        return 1;

    field = (*env)->GetFieldID(env, clazz, name, "J");
    (*env)->DeleteLocalRef(env, clazz);

    if (field == NULL)
        return 1;

    *value = (*env)->GetLongField(env, obj, field);

    return 0;
}


jint setObjectLongField(JNIEnv * env, jobject obj, const char *name, jlong value)
{
    jclass clazz;
    jfieldID field;

    clazz = (*env)->GetObjectClass(env, obj);
    if (clazz == NULL)
        return 1;

    field = (*env)->GetFieldID(env, clazz, name, "J");
    (*env)->DeleteLocalRef(env, clazz);

    if (field == NULL)
        return 1;

    (*env)->SetLongField(env, obj, field, value);

    return 0;
}

jstring getObjectStringField(JNIEnv * env, jobject obj, const char *name)
{
    jclass clazz;
    jfieldID field;

    clazz = (*env)->GetObjectClass(env, obj);
    if (clazz == NULL)
        return NULL;

    field = (*env)->GetFieldID(env, clazz, name, "Ljava/lang/String;");
    (*env)->DeleteLocalRef(env, clazz);

    if (field == NULL)
        return NULL;

    return (*env)->GetObjectField(env, obj, field);
}

jint setObjectObjectField(JNIEnv * env, jobject obj,const char *name, char *fieldClassName, jobject value)
{
    jclass clazz;
//     , fieldClazz;
    jfieldID field;
    jstring str;

    clazz = (*env)->GetObjectClass(env, obj);
    if (clazz == NULL)
        return 1;
    
    field = (*env)->GetFieldID(env, clazz, name, fieldClassName);
    
    (*env)->DeleteLocalRef(env, clazz);

    if (field == NULL)
        return 1;

    (*env)->SetObjectField(env, obj, field, value);

    return 0;
}



jint setObjectStringField(JNIEnv * env, jobject obj, const char *name, const char *value)
{
    jclass clazz;
    jfieldID field;
    jstring str;

    clazz = (*env)->GetObjectClass(env, obj);
    if (clazz == NULL)
        return 1;

    field = (*env)->GetFieldID(env, clazz, name, "Ljava/lang/String;");
    (*env)->DeleteLocalRef(env, clazz);

    if (field == NULL)
        return 1;

    str = (*env)->NewStringUTF(env, value);
    if (str == NULL)
        return 1;

    (*env)->SetObjectField(env, obj, field, str);
    (*env)->DeleteLocalRef(env, str);
    return 0;
}

jint getObjectIntField(JNIEnv * env, jobject obj, const char *name, jint *value)
{
    jclass clazz;
    jfieldID field;

    clazz = (*env)->GetObjectClass(env, obj);
    if (clazz == NULL)
        return 1;

    field = (*env)->GetFieldID(env, clazz, name, "I");
    (*env)->DeleteLocalRef(env, clazz);

    if (field == NULL)
        return 1;

    *value = (*env)->GetIntField(env, obj, field);

    return 0;
}

int retrieveEnv(JNIEnv* env)
{
    JNIEnv* myEnv = NULL;

    int result;
    if (!g_vm)
    {
        fprintf(stderr, "jahspotify::retrieveEnv: no vm available\n");
        goto fail;
    }

    result = (*g_vm)->GetEnv(g_vm,(void**)&myEnv, JNI_VERSION_1_4);

    if (result == JNI_EDETACHED)
    {
        result = (*g_vm)->AttachCurrentThread(g_vm,(void**)&myEnv, NULL);
    }

    if (result != JNI_OK)
    {
        fprintf(stderr,"jahspotify::retrieveEnv: failed to retrieve envoronment/attach vm result: 0x%d: env: 0x%x\n", result, (int)myEnv);
        goto fail;
    }

    *env = (JNIEnv)myEnv;

    return JNI_TRUE;

fail:
    return JNI_FALSE;

}

JNIEXPORT
jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;
    jclass aClass;

    g_vm = vm;
    
    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK)
    {
        fprintf(stderr,"jahspotify::JNI_OnLoad: GetEnv for JNI_OnLoad failed\n");
        goto error;
    }

    aClass = (*env)->FindClass(env, "jahspotify/impl/NativeLogger");
    if (aClass == NULL)
    {
        fprintf(stderr,"jahspotify::JNI_OnLoad: could not load jahnotify.impl.NativeLogger\n");
        goto error;
    }
    g_loggerClass = (*env)->NewGlobalRef(env,aClass);

    log_debug("jnihelpers","JNI_OnLoad","vm: 0x%x env: 0x%x",(int)g_vm,(int)env); 

    aClass = (*env)->FindClass(env, "jahspotify/impl/NativePlaybackListener");
    if (aClass == NULL)
    {
        log_error("jahspotify","JNI_OnLoad","Could not load jahnotify.impl.NativePlaybackListener");
        goto error;
    }
    g_playbackListenerClass = (*env)->NewGlobalRef(env,aClass);
    
    aClass = (*env)->FindClass(env, "jahspotify/impl/NativeLibraryListener");
    if (aClass == NULL)
    {
        log_error("jahspotify","JNI_OnLoad","Could not load jahnotify.impl.NativeLibraryListener");
        goto error;
    }
    g_libraryListenerClass = (*env)->NewGlobalRef(env,aClass);

    aClass = (*env)->FindClass(env, "jahspotify/SearchResult");
    if (aClass == NULL)
    {
        log_error("jahspotify","JNI_OnLoad","Could not load jahspotify.SearchResult");
        goto error;
    }
    g_nativeSearchResultClass = (*env)->NewGlobalRef(env,aClass);
    
    
    aClass = (*env)->FindClass(env, "jahspotify/impl/NativeConnectionListener");
    if (aClass == NULL)
    {
        log_error("jahspotify","JNI_OnLoad","Could not load jahspotify.NativeConnectionListener");
        goto error;
    }
    g_connectionListenerClass = (*env)->NewGlobalRef(env,aClass);
    
    aClass = (*env)->FindClass(env, "jahspotify/impl/NativeMediaLoadedListener");
    if (aClass == NULL)
    {
        log_error("jahspotify","JNI_OnLoad","Could not load jahspotify.NativeMediaLoadedListener");
        goto error;
    }
    g_mediaLoadedListenerClass = (*env)->NewGlobalRef(env,aClass);
    
    aClass = (*env)->FindClass(env, "jahspotify/impl/NativeSearchCompleteListener");
    if (aClass == NULL)
    {
        log_error("jahspotify","JNI_OnLoad","Could not load jahnotify.impl.NativeSearchCompleteListener");
        goto error;
    }
    g_searchCompleteListenerClass = (*env)->NewGlobalRef(env,aClass);

    aClass = (*env)->FindClass(env, "jahspotify/media/Link");
    if (aClass == NULL)
    {
        log_error("jahspotify","JNI_OnLoad","Could not load jahspotify.media.Link");
        goto error;
    }
    g_linkClass = (*env)->NewGlobalRef(env,aClass);

    /* success -- return valid version number */
    result = JNI_VERSION_1_4;
    goto exit;

error:
    result = 0;
    log_error("jahspotify","JNI_OnLoad","Error occured during initialization");
exit:
    log_debug("jahspotify","JNI_OnLoad","Exiting (result=0x%x)", result);
    return result;
}
