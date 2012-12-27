/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Solver;

/**
 *
 * @author hvar90
 */
import lpsolve.*;

/**
 *
 * @author hvar90
 */
public class Solver_basuro {

    int numCities = 4;
    int sizeGrid = 10;
    //ciudades ejemplo 1 campus virtual
    int posCities[] = {3, 7, 5, 6, 10, 6, 8, 10};
     //ciudades ejemplo 2 campus virtual
    //int posCities[] = {1, 0, 2, 3, 8, 0, 2, 7, 2, 8, 3, 10, 5, 8, 5, 9, 7, 9, 8, 8};
    // posiccion basurero
    int posDump = (numCities * 2) + 2;
    // posiccion ciudad cercana
    int posNearbyCity = 4 + (numCities * 4);
    // posicion diferencia entre ciudad cercana y basurero
    int posDelta = 6 + (numCities * 4);
    // posicion variables binarias utilizadas en la funcion objetivo
    int posBinaryObj = (8 + (numCities * 4));
    //posicion variables binarias que garantizan que la ciudad cercana es una de las ciudades dadas como entradas
    int posBinaryCity = 10 + (numCities * 4);
    LpSolve lp;
    int Ncol, j, ret, M, Mobj = 0;

    public Solver_basuro() throws LpSolveException {

        int Nvar_objetive, Nvar_binary, Nvar_binaryCity, Nvar_delta_city = 0;
        // lo maximo que puede ser un delta es el tamaño de la grilla por eso M puede ser (tamañoGrilla + 1)
        M = sizeGrid + 1;
        Mobj = sizeGrid * 2;


        //numero de variables en la funcion objetivo:
        //posicion del basurero (Xb,Yb)
        //posicion ciudad cercana  (Xc,Yc)
        //dx y dy que remplazaran las diferencias de (dx = Xc - Xb) y (dy = Yc - Yb)
        //posicion ciudad cercana  (Xc,Yc) //remplazo de las diferencias entre basurero y ciudad cercana Zx y Zy
        //binarias para sacar los valores absolutos en las distancias X y Y entre ciudad cercana y basurero Bx0 y By0
        Nvar_objetive = 10;
        //numero de variables binarias en las restricciones:
        // 2 variables binarias para sacar los valores absolutos en las distancias X y Y entre cada ciudad y basurero Bxi y Byi
        Nvar_binary = numCities * 2;
        //variables binarias que garantizan que la ciudad cercana es una de las ciudades dadas como entradas
        Nvar_binaryCity = numCities;
        //numero de variables enteras en las restricciones:
        //remplazo de las diferencias entre basurero y cada ciudad Zxi y Zyi
        Nvar_delta_city = numCities * 2;
        //total variables en el modelo
        Ncol = Nvar_objetive + Nvar_binary + Nvar_delta_city + Nvar_binaryCity;

        lp = LpSolve.makeLp(0, Ncol);
        lp.setAddRowmode(true); //sirve para construir el modelo mas rapido

        //resttriccion obvia Xb <= sizeGrid y Yb <= sizeGrid
        for (int i = 0; i < 2; i++) {

            int[] colno = new int[Ncol];
            double[] row = new double[Ncol];

            colno[ i + posDump] = 1 + i + posDump;
            row[ i + posDump] = 1;

            lp.addConstraintex(Ncol, row, colno, LpSolve.LE, sizeGrid + 1);

        }

    }

    void Create_restrictions() throws LpSolveException {



        lp.setColName(1, "Zx");
        lp.setColName(2, "Zy");
        int[] colno = new int[Ncol];
        double[] row = new double[Ncol];
        j = 2;


        int aux = 0;

        for (int idCity = 1; idCity <= numCities; idCity++) {

            //aqui se indica la columna
            colno[0] = 1;
            //aqui se indica la constante que acompaña la  variable
            row[0] = -1;

            colno[1] = 2;
            row[1] = -1;



            lp.setColName(j + 1, "Zx" + idCity);
            lp.setColName(j + 2, "Zy" + idCity);

            /* se crea cada restriccion del tipo (Zxi + Zyi  >= Zx + Zy ) */
            /* normalizado  (Zxi + Zyi - Zx - Zy  >= 0 ) */
            replace_val_abs(0);// variable X
            restrict_pos_cYc(idCity, 0, aux);
            eliminate_neg_var("x", idCity, 0);

            colno[j] = j + 1;
            row[j++] = 1;

            replace_val_abs(1);// variable Y
            restrict_pos_cYc(idCity, 1, aux);
            eliminate_neg_var("y", idCity, 1);
            colno[j] = j + 1;
            row[j++] = 1;


            lp.addConstraintex(Ncol, row, colno, LpSolve.GE, 0);

            colno = new int[Ncol];
            row = new double[Ncol];

            lp.setColName(posBinaryCity + idCity, "Bc" + idCity);
            lp.setBinary(posBinaryCity + idCity, true);
            aux++;
            aux++;




        }

        //despues de ingresar los Zxi y Zyi ingreso las 2 variables de la posicion del basurero
        lp.setColName(j + 1, "Xb");
        lp.setColName(j + 2, "Yb");

        //decimos que la suma de las 4 variables binarias Bc1 + Bc2 + Bc3 + Bc4 = 1 
        colno = new int[Ncol];
        row = new double[Ncol];
        j = 0;

        colno[posBinaryCity + j] = posBinaryCity + j + 1;
        row[posBinaryCity + (j++)] = 1;
        colno[posBinaryCity + j] = posBinaryCity + j + 1;
        row[posBinaryCity + (j++)] = 1;
        colno[posBinaryCity + j] = posBinaryCity + j + 1;
        row[posBinaryCity + (j++)] = 1;
        colno[posBinaryCity + j] = posBinaryCity + j + 1;
        row[posBinaryCity + (j++)] = 1;

        lp.addConstraintex(Ncol, row, colno, LpSolve.EQ, 1);




    }

    void restrict_pos_cYc(int idBinaryCity, int type, int aux) throws LpSolveException {
        //aca se restringe los valores que puede tomar las variables Xc y Yc osea la posicion de la ciudad cercana
        /* se crea cada restriccion del tipo (Xc = Bci*Xi ) */
        /* normalizado  (Xc - Bci*Xi  =  0 ) */
        /* igual para la variable Y  (Yc - Bci*Yi  =  0 ) */


        int[] colno = new int[Ncol];
        double[] row = new double[Ncol];
        idBinaryCity--;

        colno[posBinaryCity + idBinaryCity] = posBinaryCity + idBinaryCity + 1;
        row[posBinaryCity + idBinaryCity] = -(posCities[aux + type]);



        colno[posNearbyCity + type] = posNearbyCity + 1 + type;
        row[posNearbyCity + type] = 1;

        lp.addConstraintex(Ncol, row, colno, LpSolve.GE, 0);



    }

    void replace_val_abs(int type) throws LpSolveException {


        /* se crea cada restriccion del tipo (Zxi = Xi  - Xb ) */
        /* normalizado  (Zxi + Xb  =  Xi ) */
        /* si es una varialble Zyi entonces seria  (Zyi + Yb  = Yi ) */

        int[] colno = new int[Ncol];
        double[] row = new double[Ncol];

        colno[j] = j + 1;
        row[j] = 1;
        // lp.setUnbounded(j + 1);




        colno[posDump + type] = posDump + 1 + type;
        row[posDump + type] = 1;

        // System.out.println(posCities[j - 2]);

        lp.addConstraintex(Ncol, row, colno, LpSolve.EQ, posCities[j - 2]);

    }

    void eliminate_neg_var(String type, int idBinary, int typeInt) throws LpSolveException {


        /* se crea dos restricciones del tipo (Zxi + MB >= Zx ) y (Zxi + MB <= M - Zx ) */
        /* se crea dos restricciones del tipo (Zxi + MB - Zx>= 0 ) y (Zxi + MB - Zx <= M  ) */
        /* la variable tambien puede ser Zyi */
        int[] colno = new int[Ncol];
        double[] row = new double[Ncol];


        colno[j] = j + 1;
        row[j] = 1;

        colno[j + posDump] = 1 + j + posDump;
        row[ j + posDump] = M;

        colno[typeInt] = 1 + typeInt;
        row[typeInt] = -1;

        lp.setBinary(1 + j + posDump, true);
        lp.setColName(1 + j + posDump, "B" + type + (idBinary));


        lp.addConstraintex(Ncol, row, colno, LpSolve.GE, 0);

        colno = new int[Ncol];
        row = new double[Ncol];


        colno[j] = j + 1;
        row[j] = 1;

        colno[j + posDump] = 1 + j + posDump;
        row[ j + posDump] = M;

        colno[typeInt] = 1 + typeInt;
        row[typeInt] = -1;




        lp.addConstraintex(Ncol, row, colno, LpSolve.LE, M);

    }

    void Create_func_obj() throws LpSolveException {

        replace_val_abs_obj();

        lp.setAddRowmode(false); //se debe hacer esto cuando se hallan agregado todas las restricciones

        /* funcion objetivo de la forma (Zx + Zy) */

        int[] colno = new int[Ncol];
        double[] row = new double[Ncol];

        colno[posDelta] = posDelta + 1;
        row[posDelta] = 1;

        colno[posDelta + 1] = posDelta + 2;
        row[posDelta + 1] = 1;


        lp.setObjFnex(Ncol, row, colno);

        lp.setMaxim();

    }

    void replace_val_abs_obj() throws LpSolveException {

        //variables de la posicion de la ciudad cercana
        lp.setColName(1 + posNearbyCity, "Xc");
        lp.setColName(2 + posNearbyCity, "Yc");

        /* crear dx y dy de la forma (dx = Xc - Xb) */
        /* normalizado (dx - Xc + Xb = 0) */
        lp.setColName(1 + posDelta, "dx");
        lp.setColName(2 + posDelta, "dy");

        // se indica que los deltas pueden ser negativos
        lp.setUnbounded(1 + posDelta);
        lp.setUnbounded(2 + posDelta);
       


        for (int i = 0; i < 2; i++) {

            int[] colno = new int[Ncol];
            double[] row = new double[Ncol];

            colno[ i + posDelta] = 1 + i + posDelta;
            row[ i + posDelta] = 1;

            colno[ i + posNearbyCity] = 1 + i + posNearbyCity;
            row[ i + posNearbyCity] = -1;

            colno[ i + posDump] = 1 + i + posDump;
            row[ i + posDump] = 1;

            lp.addConstraintex(Ncol, row, colno, LpSolve.EQ, 0);

            colno = new int[Ncol];
            row = new double[Ncol];
            
             //reemplazar dx y dy por Zx y Zy
         /* crear 4 restricciones de la forma:
         * (dx + MB - Zx >= 0) NOTA; si quitamos las 2 primeras restricciones tambien arroja el mismo resultado
         * (dx + MB + Zx <= M)
         * (dx  <= Zx) ------> (dx - Zx <= 0)
         * (-dx  <= Zx)  ------> (-dx - Zx <= 0)
        lo mismo para la Y*/

            colno[i + posDelta] = 1 + i + posDelta;
            row[ i + posDelta] = 1;

            colno[ i + posBinaryObj] = 1 + i + posBinaryObj;
            row[  i + posBinaryObj] = Mobj;

            lp.setBinary(1 + i + posBinaryObj, true);

            colno[ i] = 1 + i;
            row[ i] = -1;

            lp.addConstraintex(Ncol, row, colno, LpSolve.GE, 0);

            colno = new int[Ncol];
            row = new double[Ncol];

            colno[i + posDelta] = 1 + i + posDelta;
            row[ i + posDelta] = 1;

            colno[ i + posBinaryObj] = 1 + i + posBinaryObj;
            row[  i + posBinaryObj] = Mobj;

            colno[ i] = 1 + i;
            row[ i] = 1;

            lp.addConstraintex(Ncol, row, colno, LpSolve.LE, Mobj);

            colno = new int[Ncol];
            row = new double[Ncol];

            colno[ i + posDelta] = 1 + i + posDelta;
            row[ i + posDelta] = 1;

            colno[ i] = 1 + i;
            row[ i] = -1;

            lp.addConstraintex(Ncol, row, colno, LpSolve.LE, 0);


            row[ i + posDelta] = -1;

            lp.addConstraintex(Ncol, row, colno, LpSolve.LE, 0);


        }

        lp.setColName(2 + posBinaryObj, "By0");
        lp.setColName(1 + posBinaryObj, "Bx0");

    }

    void Create_sol() throws LpSolveException {

        //generar modelo en formato lp
        lp.writeLp("model.lp");

        /*para ver mensajes importantes mientras se resuelve el model*/
        lp.setVerbose(LpSolve.IMPORTANT);

        /* calcular la solucion */
        ret = lp.solve();
        if (ret == LpSolve.OPTIMAL) {
            ret = 0;
        } else {
            ret = 5;
        }


    }

    void Print_sol() throws LpSolveException {

        // lp.printLp();
        System.out.println("Objective value: " + lp.getObjective());


        double[] row = new double[Ncol];
        lp.getVariables(row);
        for (j = 0; j < Ncol; j++) {
            System.out.println(lp.getColName(j + 1) + ": " + row[j]);
        }

        lp.deleteLp();

    }

    public static void main(String[] args) {



        try {

            Solver_basuro obj = new Solver_basuro();
            obj.Create_restrictions();
            obj.Create_func_obj();
            obj.Create_sol();
            obj.Print_sol();

        } catch (LpSolveException e) {
            e.printStackTrace();
        }
        // TODO code application logic here
    }
}
