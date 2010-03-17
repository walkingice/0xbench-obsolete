#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <sched.h>
#include <sys/resource.h>

#include <EGL/egl.h>
#include <GLES/gl.h>
#include <GLES/glext.h>

#include <utils/Timers.h>

#include <ui/FramebufferNativeWindow.h>
#include <ui/EGLUtils.h>

using namespace android;

struct _Paster{
    int x;
    int y;
    int w;
    int h;
    int vertical;
    int horizontal;
};

typedef struct _Paster Paster;

void gl_textures_generate(int texNum, GLuint* texName,
	char* texels,int tex_width, int tex_height);
void fill_texture(char* dst, int width, int height, int space);
void init_paster(Paster* paster, int w, int h, int screen_w, int screen_h);
void update_pasters(Paster** paster, int number, int screen_width, int screen_height);
void update_paster_position(Paster* paster, int screen_w, int screen_h);

void draw_paster(EGLDisplay dpy, EGLSurface surface,
	Paster** paster, int paster_num, int round,
	int screen_width, int screen_height, int depth);

int main(int argc, char** argv)
{
    EGLint configAttribs[] = {
         EGL_DEPTH_SIZE, 0,
         EGL_NONE
     };

     EGLint majorVersion;
     EGLint minorVersion;
     EGLContext context;
     EGLConfig config;
     EGLSurface surface;
     EGLint width, height;
     EGLDisplay dpy;
     EGLint z = 0;

     int texNum= 1; // how many textures we want to generate
     char* texels;  // Array for storing texel color
     GLuint texName[texNum];
     GLuint tex_width  = 300;
     GLuint tex_height = 200;
     GLuint tex_space  = 4; // 4 bytes RGBA

     int paster_num= 10; // how many Paster we want to generate

     int i; // temporary counter for loop
     srand(time(NULL));
     setpriority(PRIO_PROCESS, 0, -20);

     /* Initialize window and context */
     EGLNativeWindowType window = android_createDisplaySurface();

     dpy = eglGetDisplay(EGL_DEFAULT_DISPLAY);
     eglInitialize(dpy, &majorVersion, &minorVersion);

     status_t err = EGLUtils::selectConfigForNativeWindow(
             dpy, configAttribs, window, &config);
     if (err) {
         fprintf(stderr, "couldn't find an EGLConfig matching the screen format\n");
         return 0;
     }

     surface = eglCreateWindowSurface(dpy, config, window, NULL);
     context = eglCreateContext(dpy, config, NULL, NULL);
     eglMakeCurrent(dpy, surface, surface, context);
     eglQuerySurface(dpy, surface, EGL_WIDTH, &width);
     eglQuerySurface(dpy, surface, EGL_HEIGHT, &height);
     GLint dim = width < height ? width : height;

     /* fill color into texels and associate texture with texels */
     texels = (char*)malloc(tex_width * tex_height * tex_space);
     fill_texture(texels, tex_width, tex_height, tex_space);
     gl_textures_generate(texNum, texName, texels, tex_width, tex_height);

     Paster* paster[paster_num];
     for(i = 0; i < paster_num; ++i) {
	 paster[i] = (Paster *)malloc(sizeof(Paster));
	 init_paster(paster[i], tex_width, tex_height, width, height);
     }

     draw_paster(dpy, surface, paster, paster_num, 100, width, height, z);

     return 0;
}

void gl_textures_generate(int texNum, GLuint* texName,
	char* texels, int tex_width, int tex_height)
{
     GLint crop[4] = {0, tex_width, tex_height, -1 * tex_width};
     glGenTextures(texNum, texName);

     /* Bind to first texture only */
     glBindTexture(GL_TEXTURE_2D, texName[0]);
     glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
     glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
     glTexEnvx(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
     glEnable(GL_TEXTURE_2D);
     glEnable(GL_BLEND);
     glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
     glColor4f(1,1,1,1);
     glDisable(GL_DITHER);
     glShadeModel(GL_FLAT);
     glTexParameteriv(GL_TEXTURE_2D, GL_TEXTURE_CROP_RECT_OES, crop);
     glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
             tex_width, tex_height, 0, GL_RGBA, GL_UNSIGNED_BYTE, texels);
}

void fill_texture(char* dst, int width, int height, int space)
{
     int i, j;
     int abgr = 0x330000ff;
     int *ptr = (int*) dst;

     for(i = 0; i < width; i++) {
	 for(j = 0; j < height; j++) {
	     int c = (j * width + i);
	     *(ptr+c) = abgr;
	 }
     }
}

void init_paster(Paster* paster, int w, int h, int screen_w, int screen_h)
{
    int offset_w = 0;
    int offset_h = 0;

    if(screen_w > w) {
	offset_w = screen_w - w;
    }
    if(screen_h > h) {
	offset_h = screen_h - h;
    }

    paster->x = rand() % offset_w;
    paster->y = rand() % offset_h;
    paster->w = w;
    paster->h = h;
    paster->horizontal = rand() % 3 - 1;
    paster->vertical   = rand() % 3 - 1;
    update_paster_position(paster, screen_w, screen_h);
}

void update_paster_position(Paster* paster, int screen_w, int screen_h)
{
    int x = paster->x;
    int y = paster->y;
    int w = paster->w;
    int h = paster->h;
    int hV = paster->horizontal;
    int vV = paster->vertical;

    if(x + w + hV > screen_w) {
	paster->horizontal = -1;
    }
    if(x + hV < 0) {
	paster->horizontal = 1;
    }
    if(y + h + vV > screen_h) {
	paster->vertical = -1;
    }
    if(y + vV < 0) {
	paster->vertical= 1;
    }

    paster->x = x + paster->horizontal;
    paster->y = y + paster->vertical;
}

void update_pasters(Paster** paster, int number, int screen_width, int screen_height)
{
    int i = 0;
    for (i = 0; i < number; ++i) {
	update_paster_position(paster[i], screen_width, screen_height);
    }
}

void draw_paster(EGLDisplay dpy, EGLSurface surface,
	Paster** paster, int paster_num, int round,
	int screen_width, int screen_height, int depth)
{
     int r = 0;
     int i = 0;

     long long start_time;
     long long end_time;
     long long total_time;

     glClear(GL_COLOR_BUFFER_BIT);
     total_time = 0;

     for (r=0; r < round; r ++) {
	 glClear(GL_COLOR_BUFFER_BIT);
	 update_pasters(paster, paster_num, screen_width, screen_height);

	 start_time = systemTime();
	 for (i=0 ; i<paster_num ; i++) {
	     glDrawTexiOES(paster[i]->x
		     , paster[i]->y
		     , depth
		     , paster[i]->w
		     , paster[i]->h);
	 }
	glFinish();
	eglSwapBuffers(dpy, surface);
	end_time = systemTime();
	total_time += (end_time - start_time);
     }


     long long total_us = total_time / 1000;
     printf("Repeat drawing for %d times, spend %llu us, average: %llu us per drawing\n",
	     round, total_us, total_us/round);
     fflush(stdout);

     return;
}

