package com.tedu.element;

import com.tedu.show.GameJFrame;

import java.awt.*;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;

public class Enemy extends ElementObj {
    private List<ElementObj> elementObjs;
    private int speedX; // 横向移动速度
    private int speedY; // 纵向移动速度
    private int score;
    private int turnCoolDown; // 转向冷却计数器
    private static final int TURN_COOLDOWN_TIME = 20; // 转向冷却时间

    public Enemy(List<ElementObj> elementObjs) {
        this.elementObjs = elementObjs;
        this.score = 0; // 初始化得分为0
        this.turnCoolDown = 0;

        // 确保初始速度不为0
        Random random = new Random();
        do {
            speedX = random.nextInt(5) - 2; // -2到2
            speedY = random.nextInt(5) - 2; // -2到2
        } while (speedX == 0 && speedY == 0);
    }

    @Override
    public void increaseScore() {
        score += 1; // 增加分数
    }

    @Override
    public void showElement(Graphics g) {
        g.drawImage(this.getIcon().getImage(), this.getX(), this.getY(), this.getW(), this.getH(), null);
    }

    @Override
    protected void move() {
        // 减少冷却计数
        if (turnCoolDown > 0) {
            turnCoolDown--;
        }

        // 根据速度值更新敌人的位置
        int newX = this.getX() + speedX;
        int newY = this.getY() + speedY;

        // 检查位置是否越界
        if (newX < 0) {
            newX = 0;
            handleBoundaryCollision(true); // 水平边界碰撞
        } else if (newX >  GameJFrame.GameX - 50) {
            newX = GameJFrame.GameX - 50;
            handleBoundaryCollision(true); // 水平边界碰撞
        }

        if (newY < 0) {
            newY = 0;
            handleBoundaryCollision(false); // 垂直边界碰撞
        } else if (newY > GameJFrame.GameY - 70) {
            newY = GameJFrame.GameY - 70;
            handleBoundaryCollision(false); // 垂直边界碰撞
        }

        // 检测与其他元素的碰撞
        boolean collision = false;
        for (ElementObj obj : elementObjs) {
            if (new ElementObj(newX, newY, this.getW(), this.getH(), null) {
                @Override
                public void showElement(Graphics g) {
                    // 空实现，仅用于碰撞检测
                }
            }.pk(obj)) {
                collision = true;
                if (turnCoolDown == 0) {
                    handleElementCollision(obj);
                    turnCoolDown = TURN_COOLDOWN_TIME;
                }
                break;
            }
        }

        if (!collision) {
            // 更新敌人的位置
            this.setX(newX);
            this.setY(newY);

            // 限制只能上下左右移动
            if (Math.abs(speedX) > Math.abs(speedY)) {
                speedY = 0; // 将纵向速度置为0
                if (speedX < 0) {
                    this.setIcon(new ImageIcon("image/tank/bot/bot_left.png")); // 设置向左移动的图片
                } else {
                    this.setIcon(new ImageIcon("image/tank/bot/bot_right.png")); // 设置向右移动的图片
                }
            } else {
                speedX = 0; // 将横向速度置为0
                if (speedY < 0) {
                    this.setIcon(new ImageIcon("image/tank/bot/bot_up.png")); // 设置向上移动的图片
                } else {
                    this.setIcon(new ImageIcon("image/tank/bot/bot_down.png")); // 设置向下移动的图片
                }
            }
        }
    }

    // 处理边界碰撞
    private void handleBoundaryCollision(boolean isHorizontal) {
        if (isHorizontal) {
            speedX = -speedX; // 反转水平方向
        } else {
            speedY = -speedY; // 反转垂直方向
        }
        turnCoolDown = TURN_COOLDOWN_TIME;
    }

    // 处理与其他元素的碰撞
    private void handleElementCollision(ElementObj obstacle) {
        Random random = new Random();

        // 确保至少有一个方向的速度不为0
        if (speedX != 0) { // 横向移动时碰撞
            speedX = 0;
            // 确保纵向速度不为0
            do {
                speedY = random.nextInt(5) - 2; // -2到2
            } while (speedY == 0);
        } else if (speedY != 0) { // 纵向移动时碰撞
            speedY = 0;
            // 确保横向速度不为0
            do {
                speedX = random.nextInt(5) - 2; // -2到2
            } while (speedX == 0);
        } else {
            // 两个方向速度都为0的罕见情况，随机选择一个方向
            if (random.nextBoolean()) {
                do {
                    speedX = random.nextInt(5) - 2;
                } while (speedX == 0);
                speedY = 0;
            } else {
                do {
                    speedY = random.nextInt(5) - 2;
                } while (speedY == 0);
                speedX = 0;
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
            x = ran.nextInt(GameJFrame.GameX - 35); // 在游戏区域内随机生成X坐标
            y = ran.nextInt(GameJFrame.GameY - 35); // 在游戏区域内随机生成Y坐标

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
        this.setIcon(new ImageIcon("image/tank/bot/bot_left.png"));

        // 确保初始速度不为0
        do {
            speedX = ran.nextInt(5) - 2; // -2到2
            speedY = ran.nextInt(5) - 2; // -2到2
        } while (speedX == 0 && speedY == 0);

        return this;
    }

    // 辅助方法，用于检查两个矩形是否重叠
    private boolean checkOverlap(int x1, int y1, int x2, int y2, int w2, int h2) {
        return x1 < x2 + w2 && x1 + 35 > x2 && y1 < y2 + h2 && y1 + 35 > y2;
    }
}