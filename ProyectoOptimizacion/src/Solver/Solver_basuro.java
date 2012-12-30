/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Solver;

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
    int posNearbyCity = 4 + (numCities * 6);
    // posicion diferencia entre ciudad cercana y basurero
    int posDelta = 6 + (numCities * 6);
    // posicion variables binarias utilizadas en la funcion objetivo
    int posBinaryObj = (8 + (numCities * 6));
    //posicion variables binarias que garantizan que la ciudad cercana es una de las ciudades dadas como entradas
    int posBinaryCity = 10 + (numCities * 6);
    // posicion variables auxiliares que remplazaran la diferencia entre cada ciudad  y el basurero
    int posVarDeltaAux = 10 + (numCities * 7);
    int posVarBinary = 4 + (numCities * 2);
    //  int auxJdelta = posVarDeltaAux;
    LpSolve lp;
    int Ncol, j, ret, M, Mobj = 0;

    public Solver_basuro() throws LpSolveException {

        int Nvar_objetive, Nvar_binary, Nvar_binaryCity, Nvar_delta_city, Nvar_delta_city_aux = 0;
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
        // 4 variables binarias para sacar los valores absolutos en las distancias X y Y entre cada ciudad y basurero Bxi y Byi
        Nvar_binary = numCities * 4;
        //variables binarias que garantizan que la ciudad cercana es una de las ciudades dadas como entradas
        Nvar_binaryCity = numCities;
        //numero de variables enteras en las restricciones:
        //remplazo de las diferencias entre basurero y cada ciudad Zxi y Zyi
        Nvar_delta_city = numCities * 2;
        //variables auxiliares utilizadas para sacar los valores absolutos en las restricciones
        Nvar_delta_city_aux = numCities * 4;


        //total variables en el modelo
        Ncol = Nvar_objetive + Nvar_binary + Nvar_delta_city + Nvar_binaryCity + Nvar_delta_city_aux;

        lp = LpSolve.makeLp(0, Ncol);
        lp.setAddRowmode(true); //sirve para construir el modelo mas rapido

        //resttriccion obvia Xb <= sizeGrid y Yb <= sizeGrid
        for (int i = 0; i < 2; i++) {

            int[] colno = new int[Ncol];
            double[] row = new double[Ncol];

            colno[ i + posDump] = 1 + i + posDump;
            row[ i + posDump] = 1;

            lp.addConstraintex(Ncol, row, colno, LpSolve.LE, sizeGrid);

            colno = new int[Ncol];
            row = new double[Ncol];



            colno[ i + posNearbyCity] = 1 + i + posNearbyCity;
            row[ i + posNearbyCity] = 1;

            lp.addConstraintex(Ncol, row, colno, LpSolve.LE, sizeGrid);

        }

    }

    void Create_restrictions() throws LpSolveException {



        lp.setColName(1, "Zx");
        lp.setColName(2, "Zy");
        int[] colno = new int[Ncol];
        double[] row = new double[Ncol];
        int[] colno2 = new int[Ncol];
        double[] row2 = new double[Ncol];


        int aux = 0;

        colno[posNearbyCity] = posNearbyCity + 1;
        row[posNearbyCity] = 1;

        colno2[posNearbyCity + 1] = posNearbyCity + 2;
        row2[posNearbyCity + 1] = 1;

        //aqui se crea 2 restricciones del tipo
        //Xc= X1Bc1 + X2Bc2 + X3Bc3 .......XiBci ------> Xc - X1Bc1 - X2Bc2 - X3Bc3 .......-XiBci= 0
        //Yc= Y1Bc1 + Y2Bc2 + Y3Bc3 .......YiBci ------> Yc - Y1Bc1 - Y2Bc2 - Y3Bc3 .......-YiBci= 0
        //esto garantiza que la ciudad cercaca sea una de las ciudades dadas en el problema

        for (int j = 0; j < numCities * 2; j = j + 2) {

            colno[posBinaryCity + aux] = posBinaryCity + aux + 1;
            row[posBinaryCity + aux] = -(posCities[j]);

            colno2[posBinaryCity + aux] = posBinaryCity + aux + 1;
            row2[posBinaryCity + aux] = -(posCities[j + 1]);

            aux++;

        }

        lp.addConstraintex(Ncol, row2, colno2, LpSolve.EQ, 0);
        lp.addConstraintex(Ncol, row, colno, LpSolve.EQ, 0);


        colno = new int[Ncol];
        row = new double[Ncol];
        colno2 = new int[Ncol];
        row2 = new double[Ncol];
        j = 2;


        aux = 0;

        for (int idCity = 1; idCity <= numCities; idCity++) {

            //aqui se indica la columna
            colno[0] = 1;
            //aqui se indica la constante que acompaña la  variable
            row[0] = -1;

            colno[1] = 2;
            row[1] = -1;

            lp.setColName(j + 1, "Zx" + idCity);
            lp.setColName(j + 2, "Zy" + idCity);
            /* se crea cada restriccion del tipo (Zxia  + Zxib + Zyia + Zyib  >= Zx + Zy ) */
            /* normalizado (Zxia  + Zxib + Zyia + Zyib   - Zx - Zy >= 0 ) */
            replace_val_abs(0);
            colno[posVarDeltaAux] = posVarDeltaAux + 1;
            row[posVarDeltaAux] = 1;
            colno[posVarDeltaAux + 1] = posVarDeltaAux + 2;
            row[posVarDeltaAux + 1] = 1;
            replace_val_abs_aux(idCity, "x");// variable X
            j++;
            replace_val_abs(1);
            colno[posVarDeltaAux] = posVarDeltaAux + 1;
            row[posVarDeltaAux] = 1;
            colno[posVarDeltaAux + 1] = posVarDeltaAux + 2;
            row[posVarDeltaAux + 1] = 1;
            replace_val_abs_aux(idCity, "y");// variable Y
            j++;
            lp.addConstraintex(Ncol, row, colno, LpSolve.GE, 0);
            colno = new int[Ncol];
            row = new double[Ncol];
            lp.setColName(posBinaryCity + idCity, "Bc" + idCity);
            lp.setBinary(posBinaryCity + idCity, true);
            aux++;
            aux++;
            colno2[posBinaryCity + idCity - 1] = posBinaryCity + idCity - 1;
            row2[posBinaryCity + idCity - 1] = 1;


        }

        //despues de ingresar los Zxi y Zyi ingreso las 2 variables de la posicion del basurero
        lp.setColName(j + 1, "Xb");
        lp.setColName(j + 2, "Yb");

        //decimos que la suma de las 4 variables binarias Bc1 + Bc2 + Bc3 + Bc4 = 1 
        lp.addConstraintex(Ncol, row2, colno2, LpSolve.EQ, 1);



    }

    void replace_val_abs_aux(int idCity, String typeVar) throws LpSolveException {

        /* se crea cada restriccion del tipo (Zxi = Zxia  - Zxib ) */
        /* normalizado  (Zxi - Zxia +  Zxib  =  0 ) */
        /* si es una varialble Zyi entonces seria  (Zyi - Zyia +  Zyib  =  0) */
        /* se crea dos restricciones del tipo (  MBxia - Zxia >= 0 ) y (MBxib - Zxib >= 0 ) */
        /* lo mismo para la variable Y  */
        /* se crea la restriccion  (  Bxia + Bxib = 1 )  */
        /* se crea la restriccion  (  Byia + Byib = 1 )  */

        int[] colno3 = new int[Ncol];
        double[] row3 = new double[Ncol];
        int[] colno2 = new int[Ncol];
        double[] row2 = new double[Ncol];
        int[] colno = new int[Ncol];
        double[] row = new double[Ncol];

        colno[j] = j + 1;
        row[j] = 1;

        colno2[ posVarDeltaAux] = posVarDeltaAux + 1;
        row2[ posVarDeltaAux] = -1;
        colno3[ posVarBinary] = posVarBinary + 1;
        row3[ posVarBinary] = 1;
        colno2[ posVarBinary] = posVarBinary;
        row2[ posVarBinary++] = M;

        // lp.setInt(posVarDeltaAux, true);
        colno[posVarDeltaAux] = posVarDeltaAux + 1;
        row[posVarDeltaAux++] = -1;
        //  lp.setInt(posVarDeltaAux, true);

        lp.setColName(posVarBinary, "B" + typeVar + idCity + "a");
        lp.setBinary(posVarBinary, true);

        lp.setColName(posVarDeltaAux, "Z" + typeVar + idCity + "a");
        lp.addConstraintex(Ncol, row2, colno2, LpSolve.GE, 0);

        colno2 = new int[Ncol];
        row2 = new double[Ncol];

        colno2[ posVarDeltaAux] = posVarDeltaAux + 1;
        row2[ posVarDeltaAux] = -1;
        colno3[ posVarBinary] = posVarBinary + 1;
        row3[ posVarBinary] = 1;
        colno2[ posVarBinary] = posVarBinary;
        row2[ posVarBinary++] = M;
        colno[posVarDeltaAux] = posVarDeltaAux + 1;
        row[posVarDeltaAux++] = 1;

        lp.setColName(posVarBinary, "B" + typeVar + idCity + "b");
        lp.setBinary(posVarBinary, true);
        lp.setColName(posVarDeltaAux, "Z" + typeVar + idCity + "b");
        lp.addConstraintex(Ncol, row2, colno2, LpSolve.GE, 0);
        lp.addConstraintex(Ncol, row, colno, LpSolve.EQ, 0);
        lp.addConstraintex(Ncol, row3, colno3, LpSolve.EQ, 1);
    }

    void replace_val_abs(int type) throws LpSolveException {


        /* se crea cada restriccion del tipo (Zxi = Xi  - Xb ) */
        /* normalizado  (Zxi + Xb  =  Xi ) */
        /* si es una varialble Zyi entonces seria  (Zyi + Yb  = Yi ) */
        /* normalizado  (Zxi + Xb  =  Xi ) */
        int[] colno = new int[Ncol];
        double[] row = new double[Ncol];
        colno[j] = j + 1;
        row[j] = 1;
        lp.setUnbounded(j + 1);

        colno[posDump + type] = posDump + 1 + type;
        row[posDump + type] = 1;
        lp.addConstraintex(Ncol, row, colno, LpSolve.EQ, posCities[j - 2]);
    }

    void Create_func_obj() throws LpSolveException {

        replace_val_abs_obj();
        lp.setAddRowmode(false); //se debe hacer esto cuando se hallan agregado todas las restricciones

        /* funcion objetivo de la forma (Zx + Zy) */

        int[] colno = new int[Ncol];
        double[] row = new double[Ncol];
        colno[0] = 1;
        row[0] = 1;
        colno[1] = 2;
        row[1] = 1;
        lp.setObjFnex(Ncol, row, colno);
        lp.setMaxim();

    }

    void replace_val_abs_obj() throws LpSolveException {

        //variables de la posicion de la ciudad cercana
        lp.setColName(1 + posNearbyCity, "Xc");
        lp.setColName(2 + posNearbyCity, "Yc");
        lp.setInt(1 + posNearbyCity, true);
        lp.setInt(2 + posNearbyCity, true);
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
             * (dx + MB - Zx >= 0) 
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
