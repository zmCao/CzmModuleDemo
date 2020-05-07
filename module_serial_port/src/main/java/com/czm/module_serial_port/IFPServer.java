package com.czm.module_serial_port;

import java.lang.reflect.Method;

public class IFPServer {

	private static Object mObject = null;
//	private static WiegandImpl mWiegandImpl = new WiegandImpl();

	public enum Usb {
		Usb_1, Usb_2, Usb_3
	};

	public enum Wiegand {
		Wiegand_Input, Wiegand_Output26, Wiegand_Output34,
	};

	public enum Gpio {
		Gpio_1, Gpio_2, Gpio_3, Gpio_4
	};

	public enum Light {
		Light_InfraredLed, Light_White, Light_Green, Light_Red
	}

	public enum Device {
		Device_5V, Device_12V, Device_Relay
	}

	private static void initObject() {
		try {
			Class<?> clz = Class.forName("com.innohi.ifp.IFPServer");
			mObject = clz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean initSuccess(String params) {
		try {
			Class<?> clz = Class.forName("com.innohi.ifp.IFPServer");
			Object obj = clz.newInstance();
			Method method = clz.getDeclaredMethod("initSuccess", new Class[] { String.class });
			return (Boolean) method.invoke(obj, new Object[] { params });
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return false;
	}

	/**
	 * 设置USB状态 ， 打开或关闭
	 * 
	 * @param index
	 *            USB索引 ， 可为 1,2,3
	 * @param onOff
	 *            USB状态 ， true 为打开 ， false为关闭
	 */
	public static void setUsbState(Usb index, boolean onOff) {
		initObject();
		try {
			Class<?> clz = mObject.getClass();
			Method method = clz.getDeclaredMethod("setUsbState", new Class[] { int.class, boolean.class });
			method.invoke(mObject, new Object[] { index.ordinal(), onOff });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取USB状态
	 * 
	 * @param index
	 *            USB索引 ， 可为 1,2,3
	 * @return 返回 指定的USB设备 开/关 状态
	 */
	public static boolean getUsbState(Usb index) {
		initObject();
		try {
			Class<?> clz = mObject.getClass();
			Method method = clz.getDeclaredMethod("getUsbState", new Class[] { int.class });
			return (Boolean) method.invoke(mObject, new Object[] { index.ordinal() });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 打开韦根节点， 注意读取或写入 韦根设备前 必须打开指定的韦根节点
	 * 
	 * @param index
	 *            韦根节点索引， 0为 韦根输入设备，例如读卡器 ， 1为韦根输出设备
	 * @return
	 */
//	public static boolean openWiegand(Wiegand index) {
//		try {
//			if (index == Wiegand.Wiegand_Input) {
//				return (mWiegandImpl.openInput() > 0);
//			}
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}

	/**
	 * 关闭韦根节点， 注意读取或写入 韦根设备前 必须打开指定的韦根节点
	 * 
	 * @param index
	 *            韦根节点索引， 0为 韦根输入设备，例如读卡器 ， 1为韦根输出设备
	 */
	public static void closeWiegand(Wiegand index) {
//		try {
//			if (index == Wiegand.Wiegand_Input) {
//				mWiegandImpl.closeInput();
//			} else if (index == Wiegand.Wiegand_Output26 || index == Wiegand.Wiegand_Output34) {
//				mWiegandImpl.closeOutput();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 读取指定索引的韦根 数据
	 * 
	 * @param index
	 *            韦根索引， 只支持 index = 0
	 * @return
	 */
//	public static String readWiegand(Wiegand index) {
//		try {
//			if (index == Wiegand.Wiegand_Input) {
//				return "" + mWiegandImpl.readInput();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return "";
//	}

	/**
	 * 写入指定索引的韦根 数据
	 * 
	 * @param index
	 *            韦根索引， 只支持 index = 1
	 * @return
	 */
	public static void writeWiegand(Wiegand index, String msg) {
//		try {
//			if (index == Wiegand.Wiegand_Output26) {
//				mWiegandImpl.output26(Long.parseLong(msg));
//			} else if (index == Wiegand.Wiegand_Output34) {
//				mWiegandImpl.output34(Long.parseLong(msg));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 设置 指定GPIO状态
	 * 
	 * @param index
	 *            GPIO索引 ， 支持 设置 1/2/3/4 ， 4个通用GPIO
	 * @param onOff
	 *            GPIO状态 true为 高 ， false 为 低
	 */
	public static void setGpioState(Gpio index, boolean onOff) {
		initObject();
		try {
			Class<?> clz = mObject.getClass();
			Method method = clz.getDeclaredMethod("setGpioState", new Class[] { int.class, boolean.class });
			method.invoke(mObject, new Object[] { index.ordinal(), onOff });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取 指定GPIO状态
	 * 
	 * @param index
	 *            GPIO索引 ， 支持 设置 1/2/3/4 ， 4个通用GPIO
	 */
	public static boolean getGpioState(Gpio index) {
		initObject();
		try {
			Class<?> clz = mObject.getClass();
			Method method = clz.getDeclaredMethod("getGpioState", new Class[] { int.class });
			return (boolean) method.invoke(mObject, new Object[] { index.ordinal() });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 设置外置灯控制状态
	 * 
	 * @param index
	 *            外置灯索引 ， 支持 设置 1/2/3/4 ， 4个外置灯控制
	 * @param onOff
	 *            true 为开 ，false 为关
	 * @return
	 */
	public static void setLightState(Light index, boolean onOff) {
		initObject();
		try {
			Class<?> clz = mObject.getClass();
			Method method = clz.getDeclaredMethod("setLightState", new Class[] { int.class, boolean.class });
			method.invoke(mObject, new Object[] { index.ordinal(), onOff });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取外置灯控制状态
	 * 
	 * @param index
	 *            外置灯索引 ， 支持 设置 1/2/3/4 ， 4个外置灯控制
	 * @return
	 */
	public static boolean getLightState(Light index) {
		initObject();
		try {
			Class<?> clz = mObject.getClass();
			Method method = clz.getDeclaredMethod("getLightState", new Class[] { int.class });
			return (boolean) method.invoke(mObject, new Object[] { index.ordinal() });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 设置 外设供电状态， 如风扇， 12V输出，继电器等
	 * 
	 * @param index
	 * @param onOff
	 */
	public static void setDeviceState(Device index, boolean onOff) {
		initObject();
		try {
			Class<?> clz = mObject.getClass();
			Method method = clz.getDeclaredMethod("setDeviceState", new Class[] { int.class, boolean.class });
			method.invoke(mObject, new Object[] { index.ordinal(), onOff });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取 外设供电状态， 如风扇， 12V输出，继电器等
	 * 
	 * @param index
	 */
	public static boolean getDeviceState(Device index) {
		initObject();
		try {
			Class<?> clz = mObject.getClass();
			Method method = clz.getDeclaredMethod("getDeviceState", new Class[] { int.class });
			return (boolean) method.invoke(mObject, new Object[] { index.ordinal() });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
