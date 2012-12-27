/* Objective function */
max: +dx +dy;

/* Constraints */
R1: +Xb <= 11;
R2: +Yb <= 11;
+Zx1 +Xb = 3;
+Xc -3 Bc1 >= 0;
-Zx +Zx1 +11 Bx1 >= 0;
-Zx +Zx1 +11 Bx1 <= 11;
+Zy1 +Yb = 7;
+Yc -7 Bc1 >= 0;
-Zy +Zy1 +11 By1 >= 0;
-Zy +Zy1 +11 By1 <= 11;
-Zx -Zy +Zx1 +Zy1 >= 0;
+Zx2 +Xb = 5;
+Xc -5 Bc2 >= 0;
-Zx +Zx2 +11 Bx2 >= 0;
-Zx +Zx2 +11 Bx2 <= 11;
+Zy2 +Yb = 6;
+Yc -6 Bc2 >= 0;
-Zy +Zy2 +11 By2 >= 0;
-Zy +Zy2 +11 By2 <= 11;
-Zx -Zy +Zx2 +Zy2 >= 0;
+Zx3 +Xb = 10;
+Xc -10 Bc3 >= 0;
-Zx +Zx3 +11 Bx3 >= 0;
-Zx +Zx3 +11 Bx3 <= 11;
+Zy3 +Yb = 6;
+Yc -6 Bc3 >= 0;
-Zy +Zy3 +11 By3 >= 0;
-Zy +Zy3 +11 By3 <= 11;
-Zx -Zy +Zx3 +Zy3 >= 0;
+Zx4 +Xb = 8;
+Xc -8 Bc4 >= 0;
-Zx +Zx4 +11 Bx4 >= 0;
-Zx +Zx4 +11 Bx4 <= 11;
+Zy4 +Yb = 10;
+Yc -10 Bc4 >= 0;
-Zy +Zy4 +11 By4 >= 0;
-Zy +Zy4 +11 By4 <= 11;
-Zx -Zy +Zx4 +Zy4 >= 0;
+Bc1 +Bc2 +Bc3 +Bc4 = 1;
+Xb -Xc +dx = 0;
-Zx +dx +20 Bx0 >= 0;
+Zx +dx +20 Bx0 <= 20;
-Zx +dx <= 0;
-Zx -dx <= 0;
+Yb -Yc +dy = 0;
-Zy +dy +20 By0 >= 0;
+Zy +dy +20 By0 <= 20;
-Zy +dy <= 0;
-Zy -dy <= 0;

/* Variable bounds */
Bx1 <= 1;
By1 <= 1;
Bx2 <= 1;
By2 <= 1;
Bx3 <= 1;
By3 <= 1;
Bx4 <= 1;
By4 <= 1;
dx >= -Inf;
dy >= -Inf;
Bx0 <= 1;
By0 <= 1;
Bc1 <= 1;
Bc2 <= 1;
Bc3 <= 1;
Bc4 <= 1;

/* Integer definitions */
int Bx1,By1,Bx2,By2,Bx3,By3,Bx4,By4,Bx0,By0,Bc1,Bc2,Bc3,Bc4;
