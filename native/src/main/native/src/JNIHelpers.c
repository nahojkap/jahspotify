#include "JNIHelpers.h"

JavaVM* g_vm = NULL;

jclass g_playbackListenerClass;
jclass g_playlistListenerClass;
jclass g_connectionListenerClass;

jobject createInstance(JNIEnv *env, char *className)
{
  jclass jClass = (*env)->FindClass(env, className);
  if (jClass == NULL)
  {
    fprintf(stderr,"jahspotify::createInstance: could not load class: %s\n",className);
    return NULL;
  }
 
  jobject instance = (*env)->AllocObject(env,jClass);
  if (!instance)
  {
    fprintf(stderr,"jahspotify::createInstance: could not create instance of %s\n", className);
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

/*    fieldClazz = (*env)->GetObjectClass(env,value);
    if (fieldClazz == NULL)
    {
      return 1;
    }*/
    
/*    jmethodID msgMethodID;
    msgMethodID = (*env)->GetMethodID(env, fieldClazz, "getName","()Ljava/lang/String;");
    if(msgMethodID == NULL) 
    {
      printf("getName is returning NULL......\n");
      return 1;
    }

    jstring clsName = (jstring)(*env)->CallObjectMethod(env, obj, msgMethodID, NULL);
    
    const char* localstr = (*env)->GetStringUTFChars(env, clsName, NULL);
    printf("\n\nException class name: %s\n", localstr);    

    (*env)->ReleaseStringUTFChars(env, clsName, localstr);
    
    */
    
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

int retrieveEnv(JNIEnv* env, int *alreadyAttachedToThread)
{
    JNIEnv* myEnv = NULL;

    int result;
    if (!g_vm)
    {
        fprintf ( stderr, "jahspotify::retrieveEnv: no vm available\n");
        goto fail;
    }

    // fprintf ( stderr, "jahspotify::retrieveEnv: g_vm: 0x%x\n", g_vm);
    // fprintf ( stderr, "jahspotify::retrieveEnv: retrieving environment\n");

    result = (*g_vm)->GetEnv(g_vm,(void**)&myEnv, JNI_VERSION_1_4);
    // fprintf ( stderr, "jahspotify::retrieveEnv: environment retrieved env: 0x%x\n",myEnv);

    if (result == JNI_EDETACHED)
    {
        *alreadyAttachedToThread = JNI_FALSE;
        // fprintf(stderr,"jahspotify::retrieveEnv: detected thread not attached, attempting to attach it\n");
        result = (*g_vm)->AttachCurrentThread(g_vm,(void**)&myEnv, NULL);
        // fprintf(stderr,"jahspotify::retrieveEnv: attach completed: result: %d env: 0x%x\n",result,myEnv);
    }

    if (result != JNI_OK)
    {
        fprintf(stderr,"jahspotify::retrieveEnv: failed to retrieve envoronment/attach vm result: 0x%d: env: 0x%x\n", result, (int)myEnv);
        goto fail;
    }

    //fprintf(stderr,"jahspotify::retrieveEnv: retrieved environment: env: 0x%x already attached: %d\n",myEnv,alreadyAttachedToThread);
    *env = (JNIEnv)myEnv;

    return JNI_TRUE;

fail:
    return JNI_FALSE;

}

JNIEXPORT
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;
    jclass aClass;

    fprintf(stderr,"jahspotify::JNI_OnLoad: Entering JNI_OnLoad\n");

    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK)
    {
        fprintf(stderr,"jahspotify::JNI_OnLoad: GetEnv for JNI_OnLoad failed\n");
        goto error;
    }

    fprintf(stderr,"jahspotify::JNI_OnLoad: env: 0x%x\n",(int)env);
    g_vm = vm;

    fprintf(stderr,"jahspotify::JNI_OnLoad: vm: 0x%x\n", (int)g_vm);

    aClass = (*env)->FindClass(env, "jahspotify/PlaybackListener");
    if (aClass == NULL)
    {
        fprintf(stderr,"jahspotify::JNI_OnLoad: could not load jahnotify.PlaybackListener\n");
    }
    g_playbackListenerClass = (*env)->NewGlobalRef(env,aClass);
    
    aClass = (*env)->FindClass(env, "jahspotify/PlaylistListener");
    if (aClass == NULL)
    {
        fprintf(stderr,"jahspotify::JNI_OnLoad: could not load jahnotify.PlaylistListener\n");
    }
    g_playlistListenerClass = (*env)->NewGlobalRef(env,aClass);

    aClass = (*env)->FindClass(env, "jahspotify/ConnectionListener");
    if (aClass == NULL)
    {
        fprintf(stderr,"jahspotify::JNI_OnLoad: could not load jahnotify.ConnectionListener\n");
    }
    g_connectionListenerClass = (*env)->NewGlobalRef(env,aClass);
    

    /* success -- return valid version number */
    result = JNI_VERSION_1_4;
    goto exit;

error:
exit:
    fprintf(stderr,"jahspotify::JNI_OnLoad: exiting (result=0x%x)\n", result);
    return result;
}
