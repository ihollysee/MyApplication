package com.holly.inject.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.view.View;

import com.holly.inject.ContentView;
import com.holly.inject.ContentWidget;

public class InjectUtils {

	public static void injectObject(Object object, Activity activity) {

		Class<?> classType = object.getClass();

		// 该类是否存在ContentView类型的注解
		if (classType.isAnnotationPresent(ContentView.class)) {
			// 返回存在的ContentView类型的注解
			ContentView annotation = classType.getAnnotation(ContentView.class);

			try {

				// 返回一个 Method 对象，它反映此 Class 对象所表示的类或接口的指定公共成员方法。
				Method method = classType
						.getMethod("setContentView", int.class);
				method.setAccessible(true);
				int resId = annotation.value();
				method.invoke(object, resId);

			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// 返回 Field 对象的一个数组，这些对象反映此 Class 对象表示的类或接口声明的成员变量，
		// 包括公共、保护、默认（包）访问和私有成员变量，但不包括继承的成员变量。
		Field[] fields = classType.getDeclaredFields();

		if (null != fields && fields.length > 0) {

			for (Field field : fields) {
				// 该成员变量是否存在ContentWidget类型的注解
				if (field.isAnnotationPresent(ContentWidget.class)) {

					ContentWidget annotation = field
							.getAnnotation(ContentWidget.class);
					int viewId = annotation.value();
					View view = activity.findViewById(viewId);
					if (null != view) {
						try {
							field.setAccessible(true);
							field.set(object, view);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}

			}

		}

	}

}
