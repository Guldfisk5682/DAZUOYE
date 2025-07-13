package com.tedu.element;

import com.tedu.manager.GameLoad;
import com.tedu.show.GameJFrame;

import java.awt.*;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;

public class Boss extends ElementObj {
    private List<ElementObj> elementObjs;
    private int speedX; // 横向移动速度
    private int speedY; // 纵向移动速度
    private int health; // Boss的生命值

    public Boss(List<ElementObj> elementObjs) {
        this.elementObjs = elementObjs;
        this.health = 50; // 初始化生命值
        this.speedX = 3;
        this.speedY = 3;
    }

    @Override
    public void increaseScore() {
        // Boss一般不需要增加分数逻辑，可根据需求扩展
    }

    @Override
    public void showElement(Graphics g) {
        g.drawImage(this.getIcon().getImage(), this.getX(), this.getY(), this.getW(), this.getH(), null);
    }

    @Override
    protected void move() {
        // 在这里实现Boss的移动逻辑
        // 根据速度值更新Boss的位置
        int newX = this.getX() + speedX;
        int newY = this.getY() + speedY;

        // 检查位置是否越界
        if (newX < 0) {
            newX = 0;
            speedX = -speedX; // 反转横向速度
        } else if (newX >  GameJFrame.GameX - 50) {
            newX =  GameJFrame.GameX - 50;
            speedX = -speedX; // 反转横向速度
        }

        if (newY < 0) {
            newY = 0;
            speedY = -speedY; // 反转纵向速度
        } else if (newY > GameJFrame.GameY - 70) {
            newY = GameJFrame.GameY - 70;
            speedY = -speedY; // 反转纵向速度
        }

        // 更新Boss的位置
        this.setX(newX);
        this.setY(newY);

        // 限制只能上下左右移动
        if (Math.abs(speedX) > Math.abs(speedY)) {
            speedY = 0;
            if (speedX < 0) {
                this.setIcon(GameLoad.imgMap.get("boss_left"));
            } else {
                this.setIcon(GameLoad.imgMap.get("boss_right"));
            }
        } else {
            speedX = 0;
            if (speedY < 0) {
                this.setIcon(GameLoad.imgMap.get("boss_up"));
            } else {
                this.setIcon(GameLoad.imgMap.get("boss_down"));
            }
        }
        // 检测碰撞
        for (ElementObj obj : elementObjs) {
            if (this.pk(obj)) {
                // 碰撞发生，随机生成新的速度值
                Random random = new Random();
                speedX = random.nextInt(4) - 1; // 随机生成横向速度，范围为-1到1
                speedY = random.nextInt(4) - 1; // 随机生成纵向速度，范围为-1到1
                break;
            }
        }
    }

    public ElementObj createElement(String str) {
        Random ran = new Random();
        int x;
        int y;
        boolean overlap;

        do {
            overlap = false;
            x = ran.nextInt(GameJFrame.GameX - 50); // 在游戏区域内随机生成X坐标
            y = ran.nextInt(GameJFrame.GameY - 50); // 在游戏区域内随机生成Y坐标

            // 检查生成的位置是否与任何现有的MapObj对象重叠
            for (ElementObj obj : elementObjs) {
                if (checkOverlap(x, y, obj.getX(), obj.getY(), obj.getW(), obj.getH())) {
                    overlap = true;
                    break; // 如果发现重叠，跳出循环
                }
            }
        } while (overlap);

        this.setX(x);
        this.setY(y);
        this.setW(35);
        this.setH(35);
        this.setIcon(GameLoad.imgMap.get("boss_left"));

        return this;
    }

    // 辅助方法，用于检查两个矩形是否重叠
    private boolean checkOverlap(int x1, int y1, int x2, int y2, int w2, int h2) {
        return x1 < x2 + w2 && x1 + 50 > x2 && y1 < y2 + h2 && y1 + 50 > y2;
    }

    // 受攻击方法
    public void beAttacked(int attack) {
        this.health -= attack;
        if (this.health <= 0) {
            this.setLive(false);
        }
    }
}
