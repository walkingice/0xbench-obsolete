LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_SRC_FILES:= libmicro.c libmicro_main.c benchmark_init.c benchmark_fini.c benchmark_finirun.c benchmark_initrun.c benchmark_initbatch.c benchmark_finibatch.c benchmark_initworker.c benchmark_finiworker.c benchmark_optswitch.c benchmark_result.c 
LOCAL_MODULE:= libmicro
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
include $(LOCAL_PATH)/_Executables.mk

$(foreach TARGET_MODULE, $(EXEs), $(eval include $(LOCAL_PATH)/_Android.mk))


