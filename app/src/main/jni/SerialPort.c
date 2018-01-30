/*
 * Copyright 2009-2011 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>

#include "SerialPort.h"

#include "android/log.h"
static const char *TAG="serial_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

static speed_t getBaudrate(jint baudrate)
{
	switch(baudrate) {
	case 0: return B0;
	case 50: return B50;
	case 75: return B75;
	case 110: return B110;
	case 134: return B134;
	case 150: return B150;
	case 200: return B200;
	case 300: return B300;
	case 600: return B600;
	case 1200: return B1200;
	case 1800: return B1800;
	case 2400: return B2400;
	case 4800: return B4800;
	case 9600: return B9600;
	case 19200: return B19200;
	case 38400: return B38400;
	case 57600: return B57600;
	case 115200: return B115200;
	case 230400: return B230400;
	case 460800: return B460800;
	case 500000: return B500000;
	case 576000: return B576000;
	case 921600: return B921600;
	case 1000000: return B1000000;
	case 1152000: return B1152000;
	case 1500000: return B1500000;
	case 2000000: return B2000000;
	case 2500000: return B2500000;
	case 3000000: return B3000000;
	case 3500000: return B3500000;
	case 4000000: return B4000000;
	default: return -1;
	}
}

/*
 * Class:     android_serialport_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_android_1serialport_1api_SerialPort_open
  //(JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint flags)
		(JNIEnv *env,jclass thiz,jstring path, jint baudrate,
		 jint databits,jint stopbits,jint parity)
{
	int fd;
	speed_t speed;
	jobject mFileDescriptor;

	/* Check arguments */
	{
		speed = getBaudrate(baudrate);
		if (speed == -1) {
			/* TODO: throw an exception */
			LOGE("Invalid baudrate");
			return NULL;
		}
	}

	/* Opening device */
	{
		jboolean iscopy;
		const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
		//l  O_NOCTTY:表示打开的是一个终端设备，程序不会成为该端口的控制终端。如果不使用此标志，任务一个输入(eg:键盘中止信号等)都将影响进程。
		//l  O_NDELAY:表示不关心DCD信号线所处的状态（端口的另一端是否激活或者停止）。
//		LOGD("Opening serial port %s with flags 0x%x", path_utf, O_RDWR | flags);
//		fd = open(path_utf, O_RDWR | flags);
//		LOGD("serial_port_open,Opening serial port %s with flags 0x%x", path_utf, O_RDWR);
		fd = open(path_utf, O_RDWR );
		LOGD("open() fd = %d", fd);
		(*env)->ReleaseStringUTFChars(env, path, path_utf);
		if (fd == -1)
		{
			/* Throw an exception */
			LOGE("Cannot open port");
			/* TODO: throw an exception */
			return NULL;
		}
	}

	/* Configure device */
	{
		struct termios cfg;
		LOGD("Configuring serial port");
		if (tcgetattr(fd, &cfg))
		{
			LOGE("tcgetattr() failed");
			close(fd);
			/* TODO: throw an exception */
			return NULL;
		}

		cfmakeraw(&cfg);
		cfsetispeed(&cfg, speed);
		cfsetospeed(&cfg, speed);

		cfg.c_cflag &= ~CSIZE;
		switch (databits) /*设置数据位数*/
		{
			case 5:
				cfg.c_cflag |= CS5;
				break;
			case 6:
				cfg.c_cflag |= CS6;
				break;
			case 7:
				cfg.c_cflag |= CS7;
				break;
			case 8:
				cfg.c_cflag |= CS8;
				break;
			default:
				cfg.c_cflag |= CS8;
				break;
		}

		switch (parity)
		{
			//case 'n':
			case 0:
				cfg.c_cflag &= ~PARENB;   /* Clear parity enable */
				cfg.c_iflag &= ~INPCK;     /* Enable parity checking */
				LOGD("Configuring serial port parity N");
				break;
			//case 'o':
			case 1:
				cfg.c_cflag |= (PARODD | PARENB);  /* 设置为奇效验*/
				cfg.c_iflag |= INPCK;             /* Disnable parity checking */
				LOGD("Configuring serial port parity O");
				break;
			//case 'e':
			case 2:
				cfg.c_cflag |= PARENB;     /* Enable parity */
				cfg.c_cflag &= ~PARODD;   /* 转换为偶效验*/
				cfg.c_iflag |= INPCK;       /* Disnable parity checking */
				LOGD("Configuring serial port parity E");
				break;
			//case 'S':
			case 3:  /*as no parity*/
				cfg.c_cflag &= ~PARENB;
				cfg.c_cflag &= ~CSTOPB;
				LOGD("Configuring serial port parity S");
				break;
			default:
				cfg.c_cflag &= ~PARENB;   /* Clear parity enable */
				cfg.c_iflag &= ~INPCK;     /* Enable parity checking */
				LOGD("Configuring serial port parity S");
				break;
		}
		/* 设置停止位*/
		switch (stopbits)
		{
			case 1:
				cfg.c_cflag &= ~CSTOPB;
				break;
			case 2:
				cfg.c_cflag |= CSTOPB;
				break;
			default:
				cfg.c_cflag &= ~CSTOPB;
				break;
		}
		/* Set input parity option */
		if (parity != 0)
			cfg.c_iflag |= INPCK;

		//设置等待时间和最小接收字符
//		cfg.c_cc[VTIME] = 15; /* 读取一个字符等待1*(1/10)s */
//		cfg.c_cc[VMIN] = 0; /* 读取字符的最少个数为1 */
//		//如果发生数据溢出，接收数据，但是不再读取
//		tcflush(fd,TCIFLUSH);

		if (tcsetattr(fd, TCSANOW, &cfg))
		{
			LOGE("tcsetattr() failed");
			close(fd);
			/* TODO: throw an exception */
			return NULL;
		}
		LOGD("Configuring serial port ok");
	}

	/* Create a corresponding file descriptor */
	{
		jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
		jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V");
		jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
		mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
		(*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint)fd);
	}

	return mFileDescriptor;
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_android_1serialport_1api_SerialPort_close
  (JNIEnv *env, jobject thiz)
{
	jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
	jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

	jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
	jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");

	jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
	jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

	LOGD("close(fd = %d)", descriptor);
	close(descriptor);
}

