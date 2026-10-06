package org.arkanoid.io;

import org.arkanoid.core.App;
import org.arkanoid.core.CollisionManager;
import org.arkanoid.entity.*;
import org.arkanoid.graphics.HUD;
import org.junit.jupiter.api.*;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CollisionManager.
 * Tests collision detection, ball-wall collisions, ball-paddle collisions,
 * ball-brick collisions, and scoring logic.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CollisionManagerTest {
    private TestApp testApp;
    private Ball ball;
    private Paddle paddle;
    private List<Brick> bricks;

    // Minimal App implementation for testing
    static class TestApp extends App {
        private int score = 0;
        private int lives = 3;
        private final Paddle paddle;
        private final Ball ball;
        private final List<Brick> bricks;

        public TestApp(Paddle paddle, Ball ball, List<Brick> bricks) {
            this.paddle = paddle;
            this.ball = ball;
            this.bricks = bricks;
        }

        @Override
        public int getScore() {
            return score;
        }

        @Override
        public void addScore(int points) {
            this.score += points;
        }

        @Override
        public int getLives() {
            return lives;
        }

        @Override
        public void addLives(int x) {
            if (this.lives + x <= HUD.MAX_LIVES && this.lives + x >= 0) {
                this.lives += x;
            }
        }

        @Override
        public Paddle getPaddle() {
            return paddle;
        }

        @Override
        public Ball getBall() {
            return ball;
        }

        @Override
        public List<Brick> getBricks() {
            return bricks;
        }
    }

    @BeforeEach
    void setUp() {
        ball = new Ball(400, 300, 10, 0, 0); // Ball at center, radius 10
        paddle = new Paddle(350, 550, 100, 20, 600); // Paddle at bottom, size 100x20
        bricks = new ArrayList<>();
        testApp = new TestApp(paddle, ball, bricks);
    }

    @AfterEach
    void tearDown() {
        bricks.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Test AABB Collision Detection")
    void testIsColliding() {
        GameObject obj1 = new GameObject(100, 100, 50, 30) {
            @Override
            public void update(double deltaTime) {}
            @Override
            public void render(GraphicsContext gc) {}
        };
        GameObject obj2 = new GameObject(120, 110, 50, 30) {
            @Override
            public void update(double deltaTime) {}
            @Override
            public void render(GraphicsContext gc) {}
        };
        assertTrue(CollisionManager.isColliding(obj1, obj2), "Objects should collide when overlapping");

        GameObject obj3 = new GameObject(200, 200, 50, 30) {
            @Override
            public void update(double deltaTime) {}
            @Override
            public void render(GraphicsContext gc) {}
        };
        assertFalse(CollisionManager.isColliding(obj1, obj3), "Objects should not collide when not overlapping");
    }

    @Test
    @Order(2)
    @DisplayName("Test Ball Collision with Left Wall")
    void testBallWallCollisionLeftWall() {
        ball.setDx(-5); // Moving left
        ball.setPointX(0); // At left wall
        CollisionManager.handleBallWallCollision(ball);
        assertTrue(ball.getDx() > 0, "Ball should bounce right when hitting left wall");
        assertEquals(0, ball.getPointX(), "Ball should stay at left wall edge");
    }

    @Test
    @Order(3)
    @DisplayName("Test Ball Collision with Right Wall")
    void testBallWallCollisionRightWall() {
        ball.setDx(5); // Moving right
        ball.setPointX(App.WIDTH - ball.getWidth()); // At right wall
        CollisionManager.handleBallWallCollision(ball);
        assertTrue(ball.getDx() < 0, "Ball should bounce left when hitting right wall");
        assertEquals(App.WIDTH - ball.getWidth(), ball.getPointX(), "Ball should stay at right wall edge");
    }

    @Test
    @Order(4)
    @DisplayName("Test Ball Collision with Top Wall")
    void testBallWallCollisionTopWall() {
        ball.setDy(-1); // Moving up
        ball.setPointY(49); // At top wall (y=50)
        CollisionManager.handleBallWallCollision(ball);
        assertTrue(ball.getDy() > 0, "Ball should bounce down when hitting top wall");
        assertEquals(50, ball.getPointY(), "Ball should stay at y=50");
    }

    @Test
    @Order(5)
    @DisplayName("Test Ball Collision with Bottom Wall Loses Life")
    void testBallWallCollisionBottomWallLoseLife() {
        ball.setDy(5); // Moving down
        ball.setPointY(App.HEIGHT - ball.getHeight()); // At bottom wall
        CollisionManager.handleBallWallCollision(ball);
        assertFalse(ball.isAlive(), "Ball should be inactive when hitting bottom wall");
    }

    @Test
    @Order(6)
    @DisplayName("Test Ball Collision with Paddle Top")
    void testBallPaddleCollisionTop() {
        ball.setDy(5); // Moving down
        ball.setPointX(paddle.getPointX() + paddle.getWidth() / 2.0); // Center of paddle
        ball.setPointY(paddle.getPointY() - ball.getHeight()); // Touching paddle top
        CollisionManager.handleBallPaddleCollision(ball, paddle);
        assertTrue(ball.getDy() < 0, "Ball should bounce up when hitting paddle top");
        assertEquals(paddle.getPointY() - ball.getHeight(), ball.getPointY(), "Ball should be positioned above paddle");
    }

    @Test
    @Order(7)
    @DisplayName("Test Ball Collision with Paddle Edge Angle Adjustment")
    void testBallPaddleCollisionAngleAdjustment() {
        ball.setDy(5); // Moving down
        ball.setPointX(paddle.getPointX()); // Left edge of paddle
        ball.setPointY(paddle.getPointY() - ball.getHeight());
        CollisionManager.handleBallPaddleCollision(ball, paddle);
        assertTrue(ball.getDx() < 0, "Ball should have negative horizontal velocity when hitting paddle's left edge");
        assertTrue(ball.getDy() < 0, "Ball should bounce up");
    }

    @Test
    @Order(8)
    @DisplayName("Test Ball Collision with Normal Brick")
    void testBallBrickCollisionNormalBrick() {
        NormalBrick brick = new NormalBrick(400, 100, 50, 30, 1, "normal");
        bricks.add(brick);
        ball.setDy(-5); // Moving up
        ball.setPointX(425); // Brick center
        ball.setPointY(130); // Touching brick bottom
        CollisionManager.handleBallBricksCollision(ball, bricks);
        assertTrue(brick.isDestroyed(), "Normal brick should be destroyed");
        assertTrue(ball.getDy() > 0, "Ball should bounce down when hitting brick");
    }

    @Test
    @Order(9)
    @DisplayName("Test Ball Collision with Unbreakable Brick")
    void testBallBrickCollisionUnbreakableBrick() {
        UnbreakableBrick brick = new UnbreakableBrick(400, 100, 50, 30, 1, "unbreakable");
        bricks.add(brick);
        ball.setDy(-5); // Moving up
        ball.setPointX(425); // Brick center
        ball.setPointY(130); // Touching brick bottom
        CollisionManager.handleBallBricksCollision(ball, bricks);
        assertFalse(brick.isDestroyed(), "Unbreakable brick should not be destroyed");
        assertTrue(ball.getDy() > 0, "Ball should bounce down when hitting brick");
    }

    @Test
    @Order(10)
    @DisplayName("Test Score Increases on Normal Brick Destruction")
    void testScoreOnNormalBrickDestruction() {
        NormalBrick brick = new NormalBrick(400, 100, 50, 30, 1, "normal");
        bricks.add(brick);
        ball.setDy(-5); // Moving up
        ball.setPointX(425); // Brick center
        ball.setPointY(130); // Touching brick bottom
        int initialScore = testApp.getScore();
        CollisionManager.handleBallBricksCollision(ball, bricks);
        if (brick.isDestroyed()) {
            testApp.addScore(10); // Simulate GameLoop scoring
        }
        assertEquals(initialScore + 10, testApp.getScore(), "Score should increase by 10 when NormalBrick is destroyed");
    }

    @Test
    @Order(11)
    @DisplayName("Test Score Unchanged on Unbreakable Brick Collision")
    void testScoreUnchangedOnUnbreakableBrickCollision() {
        UnbreakableBrick brick = new UnbreakableBrick(400, 100, 50, 30, 1, "unbreakable");
        bricks.add(brick);
        ball.setDy(-5); // Moving up
        ball.setPointX(425); // Brick center
        ball.setPointY(130); // Touching brick bottom
        int initialScore = testApp.getScore();
        CollisionManager.handleBallBricksCollision(ball, bricks);
        assertEquals(initialScore, testApp.getScore(), "Score should not change on UnbreakableBrick collision");
    }

    @Test
    @Order(12)
    @DisplayName("Test Score Unchanged on Wall Collision")
    void testScoreUnchangedOnWallCollision() {
        ball.setDy(-5); // Moving up
        ball.setPointY(50); // At top wall
        int initialScore = testApp.getScore();
        CollisionManager.handleBallWallCollision(ball);
        assertEquals(initialScore, testApp.getScore(), "Score should not change on wall collision");
    }

    @Test
    @Order(13)
    @DisplayName("Test Score Unchanged on Paddle Collision")
    void testScoreUnchangedOnPaddleCollision() {
        ball.setDy(5); // Moving down
        ball.setPointX(paddle.getPointX() + paddle.getWidth() / 2.0);
        ball.setPointY(paddle.getPointY() - ball.getHeight());
        int initialScore = testApp.getScore();
        CollisionManager.handleBallPaddleCollision(ball, paddle);
        assertEquals(initialScore, testApp.getScore(), "Score should not change on paddle collision");
    }

    @Test
    @Order(14)
    @DisplayName("Test Multiple Brick Collisions Selects Closest")
    void testMultipleBrickCollisionsSelectsClosest() {
        NormalBrick brick1 = new NormalBrick(400, 100, 50, 30, 1, "normal1");
        NormalBrick brick2 = new NormalBrick(400, 150, 50, 30, 1, "normal2");
        bricks.add(brick1);
        bricks.add(brick2);
        ball.setDy(-5); // Moving up
        ball.setPointX(425); // Center of both bricks
        ball.setPointY(130); // Close to brick1
        CollisionManager.handleBallBricksCollision(ball, bricks);
        assertTrue(brick1.isDestroyed(), "Closest brick (brick1) should be destroyed");
        assertFalse(brick2.isDestroyed(), "Farther brick (brick2) should not be destroyed");
        assertTrue(ball.getDy() > 0, "Ball should bounce down");
    }

    @Test
    @Order(15)
    @DisplayName("Test Ball Collision with High HP Normal Brick")
    void testBallCollisionWithHighHPNormalBrick() {
        NormalBrick brick = new NormalBrick(400, 100, 50, 30, 2, "normal");
        bricks.add(brick);
        ball.setDy(-5); // Moving up
        ball.setPointX(425); // Brick center
        ball.setPointY(130); // Touching brick bottom
        CollisionManager.handleBallBricksCollision(ball, bricks);
        assertFalse(brick.isDestroyed(), "Normal brick with 2 HP should not be destroyed after one hit");
        assertTrue(ball.getDy() > 0, "Ball should bounce down");
        CollisionManager.handleBallBricksCollision(ball, bricks); // Hit again
        assertTrue(brick.isDestroyed(), "Normal brick should be destroyed after second hit");
    }
}