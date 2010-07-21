LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_CFLAGS:= -D_REENTRANT -D_REENTRANT -lpthread -lm

LOCAL_SRC_FILES:= getpid.c libmicro.c libmicro_main.c  benchmark_fini.c benchmark_initrun.c benchmark_finirun.c benchmark_initbatch.c benchmark_finibatch.c benchmark_initworker.c benchmark_finiworker.c benchmark_optswitch.c benchmark_result.c 
LOCAL_MODULE:= getpid
LOCAL_MODULE_TAGS := optional

include $(BUILD_EXECUTABLE)
