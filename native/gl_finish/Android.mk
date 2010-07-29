LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
	gl_finish.cpp

LOCAL_SHARED_LIBRARIES := \
	libcutils \
    libEGL \
    libGLESv1_CM \
    libui

LOCAL_MODULE:= gl_finish_test
LOCAL_MODULE_TAGS := optional

LOCAL_CFLAGS := -DGL_GLEXT_PROTOTYPES

include $(BUILD_EXECUTABLE)
