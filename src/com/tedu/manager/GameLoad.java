package com.tedu.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.swing.ImageIcon;

import com.tedu.element.Boss;
import com.tedu.element.ElementObj;
import com.tedu.element.Enemy;
import com.tedu.element.MapObj;

/**
 * @说明  加载器(工具：用户读取配置文件的工具)工具类,大多提供的是 static方法
 * @author renjj
 *
 */
public class GameLoad {
	//	得到资源管理器
	private static ElementManager em=ElementManager.getManager();

	//	图片集合  使用map来进行存储     枚举类型配合移动(扩展)
	public static Map<String,ImageIcon> imgMap = new HashMap<>();

	public static Map<String,List<ImageIcon>> imgMaps;
	private static int currentLevel = 1; // 当前关卡编号

	//	用户读取文件的类
	private static Properties pro =new Properties();




	/**
	 * @说明 传入地图id有加载方法依据文件规则自动产生地图文件名称，加载文件
	 * @param mapId  文件编号 文件id
	 */
	public static List<ElementObj> MapLoad(int mapId) {
//		得到了我们的文件路径
		String mapName="com/tedu/text/"+mapId+".map";
//		使用io流来获取文件对象   得到类加载器
		ClassLoader classLoader = GameLoad.class.getClassLoader();
		InputStream maps = classLoader.getResourceAsStream(mapName);
		if(maps ==null) {
			System.out.println("配置文件读取异常,请重新安装");
			return null;
		}
		try {
//			以后用的 都是 xml 和 json
			pro.clear();
			pro.load(maps);
//			可以直接动态的获取所有的key，有key就可以获取 value
//			java学习中最好的老师 是 java的API文档。
			Enumeration<?> names = pro.propertyNames();
			List<ElementObj> mapObjs = new ArrayList<>();
			while(names.hasMoreElements()) {//获取是无序的
//				这样的迭代都有一个问题：一次迭代一个元素。
				String key=names.nextElement().toString();
				System.out.println(pro.getProperty(key));
//				就可以自动的创建和加载 我们的地图类
				String [] arrs=pro.getProperty(key).split(";");
				for(int i=0;i<arrs.length;i++) {
					ElementObj element = new MapObj().createElement(key+","+arrs[i]);
					System.out.println(element);
					em.addElement(element, GameElement.MAPS);
					mapObjs.add(element);
				}
			}
			return mapObjs;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 *@说明 加载图片代码
	 *加载图片 代码和图片之间差 一个 路径问题
	 */
	public static void loadImg() {//可以带参数，因为不同的关也可能需要不一样的图片资源
		String texturl="com/tedu/text/GameData.pro";//文件的命名可以更加有规律
		ClassLoader classLoader = GameLoad.class.getClassLoader();
		InputStream texts = classLoader.getResourceAsStream(texturl);
//		imgMap用于存放数据
		pro.clear();
		try {
//			System.out.println(texts);
			pro.load(texts);
			Set<Object> set = pro.keySet();//是一个set集合
			for(Object o:set) {
				String url=pro.getProperty(o.toString());
				imgMap.put(o.toString(), new ImageIcon(url));
			}
			imgMap.put("boss_up", new ImageIcon("image/tank/play1/player1_up.png"));
			imgMap.put("boss_left", new ImageIcon("image/tank/play1/player1_left.png"));
			imgMap.put("boss_right", new ImageIcon("image/tank/play1/player1_right.png"));
			imgMap.put("boss_down", new ImageIcon("image/tank/play1/player1_down.png"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 加载玩家
	 */
	public static void loadPlay() {

		loadObj();
		String playStr="500,500,up";
		ElementObj obj=getObj("play");
		ElementObj play = obj.createElement(playStr);
//		ElementObj play = new Play().createElement(playStr);
//		解耦,降低代码和代码之间的耦合度 可以直接通过 接口或者是抽象父类就可以获取到实体对象
		em.addElement(play, GameElement.PLAY);
	}

	public static ElementObj getObj(String str) {
		try {
			Class<?> class1 = objMap.get(str);
			Object newInstance = class1.newInstance();
			if(newInstance instanceof ElementObj) {
				return (ElementObj)newInstance;   //这个对象就和 new Play()等价
//				新建立啦一个叫  GamePlay的类
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 扩展： 使用配置文件，来实例化对象 通过固定的key(字符串来实例化)
	 * @param args
	 */
	private static Map<String,Class<?>> objMap=new HashMap<>();

	public static void loadObj() {
		String texturl="com/tedu/text/obj.pro";//文件的命名可以更加有规律
		ClassLoader classLoader = GameLoad.class.getClassLoader();
		InputStream texts = classLoader.getResourceAsStream(texturl);
		pro.clear();
		try {
			pro.load(texts);
			Set<Object> set = pro.keySet();//是一个set集合
			for(Object o:set) {
				String classUrl=pro.getProperty(o.toString());
//				使用反射的方式直接将 类进行获取
				Class<?> forName = Class.forName(classUrl);
				objMap.put(o.toString(), forName);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	//	用于测试
	public static void main(String[] args) {
		MapLoad(5);
	}
	public static void LoadEnemy(List<ElementObj> elementObjs) {
		ImageIcon icon=new ImageIcon("/image/tank/bot/bot_up.png");
		for(int i=0;i<7;i++) {
			em.addElement(new Enemy(elementObjs).createElement(""),GameElement.ENEMY);
		}

	}
	public static void LoadBoss(List<ElementObj> elementObjs) {
//		ImageIcon icon = new ImageIcon("image/tank/play1/player1_up.png");
//		em.addElement(new Boss(elementObjs).createElement(""), GameElement.BOSS);
		Boss boss = new Boss(elementObjs);
		boss.setIcon(GameLoad.imgMap.get("boss_up"));
		em.addElement(boss.createElement(""), GameElement.BOSS);
	}

}
