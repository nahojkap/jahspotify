#include "Logging.h"
#include "JNIHelpers.h"
#include "jni_md.h"
#include "jni.h"

#include <stdlib.h>
#include <stdarg.h>
#include <stdio.h>

extern jclass g_loggerClass;

static void logToJava(const char *method, const char *component, const char *subComponent, const char *format, va_list args)
{
    jmethodID jMethod = NULL;
    JNIEnv* env = NULL;
    
    // FIXME: This should probably be a little more clever?
    char *buffer = calloc(1,1024);
    
    vsprintf (buffer, format, args );
    
    if (!retrieveEnv((JNIEnv*)&env))
    {
        goto fail;
    }
    
    jstring componentStr = (*env)->NewStringUTF(env, component);
    jstring subComponentStr = (*env)->NewStringUTF(env, subComponent);
    jstring messageStr = (*env)->NewStringUTF(env, buffer);
    
    jMethod = (*env)->GetStaticMethodID(env, g_loggerClass, method, "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    
    if (!jMethod)
    {
        goto fail;
    }
    
    (*env)->CallStaticVoidMethod(env,g_loggerClass,jMethod,componentStr,subComponentStr, messageStr);
    
fail:
exit:
    if (buffer)
    {
        free(buffer);
    }
    return;   
    
}

void log_trace(const char *component, const char *subComponent,const char *format, ...)
{
    va_list args;
    va_start(args,format);
    logToJava("trace", component, subComponent,format, args); 
    va_end(args);
}

void log_debug(const char *component,  const char *subComponent,const char *format, ...)
{
    va_list args;
    va_start(args,format);
    logToJava("debug", component, subComponent,format, args); 
    va_end(args);
}

void log_info(const char *component, const char *subComponent,char *format, ...)
{
    va_list args;
    va_start(args,format);
    logToJava("info", component, subComponent,format, args); 
    va_end(args);
}

void log_warn(const char *component, const char *subComponent,char *format, ...)
{
    va_list args;
    va_start(args,format);
    logToJava("warn", component, subComponent,format, args); 
    va_end(args);
}

void log_error(const char *component, const char *subComponent,char *format, ...)
{
    va_list args;
    va_start(args,format);
    logToJava("error", component, subComponent,format, args); 
    va_end(args);
}

void log_fatal(const char *component, const char *subComponent,char *format, ...)
{
    va_list args;
    va_start(args,format);
    logToJava("fatal", component, subComponent,format, args); 
    va_end(args);
}

