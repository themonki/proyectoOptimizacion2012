/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Solver;

/**
 *
 * @author felipe + andrea
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import lpsolve.*;
import Lectura.Lector;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Solver_basuro_andrea {

    int numCities;
    int sizeGrid;
    int[] posCities; //x,y
    // Posiciones en la matriz del simplex,
    // ejemplo: posición de la variable Xb en la matriz del simplex
    // Posde Xb = posDump y Pos de Yb = posDump +1
    int posDump; // posicion basurero
    int posNearbyCity; // posiccion ciudad cercana
    int posDelta; // posicion diferencia entre ciudad cercana y basurero
    int posBinaryObj; // posicion variables binarias utilizadas en la funcion objetivo
    int posBinaryCity; //posicion variables binarias que garantizan que la ciudad cercana es una de las ciudades dadas como entradas
    int posVarDeltaAux; // posicion variables auxiliares que remplazaran la diferencia entre cada ciudad  y el basurero
    int posVarBinary; //posicion variables binarias que aseguraran que se escoja una  de las ciudades dadas inicialmente
    LpSolve lp;
    int Ncol, j, ret, M, Mobj = 0; //j es usada para indicar la columna de una variable en la matriz simplex

    public Solver_basuro_andrea(int numCities, int sizeGrid, int[] posCities) throws LpSolveException {

        //ciudades ejemplo 1 campus virtual
        // numCities = 4
        // sizeGrid = 10
        // posCities[] = {3, 7, 5, 6, 10, 6, 8, 10};
        // x1 y1

        //ciudades ejemplo 2 campus virtual
        this.numCities = numCities;
        this.sizeGrid = sizeGrid;
        this.posCities = posCities;//{1, 0, 2, 3, 8, 0, 2, 7, 2, 8, 3, 10, 5, 8, 5, 9, 7, 9, 8, 8};

        //posiciones en matriz simplex
        posDump = (numCities * 2) + 2;
        posNearbyCity = 4 + (numCities * 6);
        posDelta = 6 + (numCities * 6);
        posBinaryObj = (8 + (numCities * 6));
        posBinaryCity = 10 + (numCities * 6);
        posVarDeltaAux = 10 + (numCities * 7);
        posVarBinary = 4 + (numCities * 2);


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

        // restricciones obvias Xb <= sizeGrid y Yb <= sizeGrid y
        // Xc <= sizeGrid y Yc <= sizeGrid
        for (int i = 0; i < 2; i++) {

            int[] colno = new int[Ncol];
            double[] row = new double[Ncol];

            colno[i + posDump] = 1 + i + posDump;
            row[i + posDump] = 1;

            lp.addConstraintex(Ncol, row, colno, LpSolve.LE, sizeGrid);

            colno = new int[Ncol];
            row = new double[Ncol];



            colno[i + posNearbyCity] = 1 + i + posNearbyCity;
            row[i + posNearbyCity] = 1;

            lp.addConstraintex(Ncol, row, colno, LpSolve.LE, sizeGrid);

        }

    }

    void Create_constraints() throws LpSolveException {

        lp.setColName(1, "Zx");
        lp.setColName(2, "Zy");
        int[] colno = new int[Ncol];
        double[] row = new double[Ncol];
        int[] colno2 = new int[Ncol];
        double[] row2 = new double[Ncol];
        int aux = 0;// como en el for de abajo se hace es por cada ciudad el aux es para la posicion de la variable 
        colno[posNearbyCity] = posNearbyCity + 1;
        row[posNearbyCity] = 1;
        colno2[posNearbyCity + 1] = posNearbyCity + 2;
        row2[posNearbyCity + 1] = 1;
        //aqui se crea 2 restricciones del tipo
        //Xc= X1*Bc1 + X2*Bc2 + X3*Bc3 .......XiBci ------> Xc - X1Bc1 - X2Bc2 - X3Bc3 .......-XiBci= 0
        //Yc= Y1*Bc1 + Y2*Bc2 + Y3*Bc3 .......YiBci ------> Yc - Y1Bc1 - Y2Bc2 - Y3Bc3 .......-YiBci= 0
        //esto garantiza que la ciudad cercaca sea una de las ciudades dadas en el problema

        for (int j = 0; j < numCities * 2; j = j + 2) {

            colno[posBinaryCity + aux] = posBinaryCity + aux + 1;
            row[posBinaryCity + aux] = -(posCities[j]);

            colno2[posBinaryCity + aux] = posBinaryCity + aux + 1;
            row2[posBinaryCity + aux] = -(posCities[j + 1]);

            aux++;

        }

        //Se agregan las restricciones.
        lp.addConstraintex(Ncol, row2, colno2, LpSolve.EQ, 0);
        lp.addConstraintex(Ncol, row, colno, LpSolve.EQ, 0);



        colno = new int[Ncol];
        row = new double[Ncol];
        colno2 = new int[Ncol];
        row2 = new double[Ncol];

        j = 2;// vale dos por que en 0 y en 1 se encuenran los zx y zy de la funcion objetivo
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

            replace_val_abs(0);// le da los valores a los zx= x_ciudad-x_basurero aun no hace nada de valor absoluto


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
            colno2[posBinaryCity + idCity] = posBinaryCity + idCity;
            row2[posBinaryCity + idCity] = 1;


        }

        //despues de ingresar los Zxi y Zyi ingreso las 2 variables de la posicion del basurero
        lp.setColName(j + 1, "Xb");
        lp.setColName(j + 2, "Yb");

        //decimos que la suma de las n variables binarias Bc1 + Bc2 + Bc3 +... + Bcn = 1 n=numero de ciudades
        lp.addConstraintex(Ncol, row2, colno2, LpSolve.EQ, 1);


    }

    void replace_val_abs_aux(int idCity, String typeVar) throws LpSolveException {

        /* se crea cada restriccion del tipo (Zxi = Zxia  - Zxib ) */
        /* normalizado  (Zxi - Zxia +  Zxib  =  0 ) */
        /* si es una varialble Zyi entonces seria  (Zyi - Zyia +  Zyib  =  0) */
        /* se crea dos restricciones del tipo (  M*Bxia - Zxia >= 0 ) y (M*Bxib - Zxib >= 0 ) */
        /* lo mismo para la variable Y  */
        /* se crea la restriccion  (  Bxia + Bxib = 1 )  */
        /* se crea la restriccion  (  Byia + Byib = 1 )  */

        //---------------------------------------------------------------------------
        int[] colno2 = new int[Ncol];
        double[] row2 = new double[Ncol];

        colno2[posVarDeltaAux] = posVarDeltaAux + 1;
        row2[posVarDeltaAux] = -1;//Zxia

        colno2[posVarBinary] = posVarBinary + 1;
        row2[posVarBinary] = M;//Bxia

        // M*Bxia - Zxia >= 0


        //-------------------------------------------------------------------

        int[] colno = new int[Ncol];
        double[] row = new double[Ncol];


        colno[j] = j + 1;
        row[j] = 1;//Zxi

        colno[posVarDeltaAux] = posVarDeltaAux + 1;
        row[posVarDeltaAux] = -1;//Zxia

        posVarDeltaAux++;//Zxib

        colno[posVarDeltaAux] = posVarDeltaAux + 1;
        row[posVarDeltaAux] = 1;//Zxib
        // (Zxi - Zxia +  Zxib  =  0 )

        //---------------------------------------------------------------------------------------------------


        int[] colno3 = new int[Ncol];
        double[] row3 = new double[Ncol];

        colno3[posVarBinary] = posVarBinary + 1;
        row3[posVarBinary] = 1;// Bxia 

        posVarBinary++;

        colno3[posVarBinary] = posVarBinary + 1;
        row3[posVarBinary] = 1;//Bxib 


        // (  Bxia + Bxib = 1 ) 


        //-------------------------------------------------------------------------------------------------
        lp.setColName(posVarBinary, "B" + typeVar + idCity + "a");// como en la matriz del lp no cuenta desde 0 posVarBinary  se le hizo ++ arriba pa apuntar Bxib pero en el lp apunta Bxia solo indices
        lp.setBinary(posVarBinary, true);

        lp.setColName(posVarDeltaAux, "Z" + typeVar + idCity + "a");// igual que posVarBinary tambien se le hizo ++ arriba  a posVarDeltaAux
        lp.addConstraintex(Ncol, row2, colno2, LpSolve.GE, 0);
        //----------------------------------------------------------------------------------------------------------


        int[] colno4 = new int[Ncol];
        double[] row4 = new double[Ncol];

        colno4[posVarDeltaAux] = posVarDeltaAux + 1;
        row4[posVarDeltaAux] = -1;


        colno4[posVarBinary] = posVarBinary + 1;
        row4[posVarBinary] = M;

        //(M*Bxib - Zxib >= 0 ) 
        posVarBinary++;
        posVarDeltaAux++;
        //----------------------------------------------------

        lp.setColName(posVarBinary, "B" + typeVar + idCity + "b");
        lp.setBinary(posVarBinary, true);
        lp.setColName(posVarDeltaAux, "Z" + typeVar + idCity + "b");
        lp.addConstraintex(Ncol, row4, colno4, LpSolve.GE, 0);
        lp.addConstraintex(Ncol, row, colno, LpSolve.EQ, 0);
        lp.addConstraintex(Ncol, row3, colno3, LpSolve.EQ, 1);



        /* colno2[ posVarDeltaAux] = posVarDeltaAux + 1;
        row2[ posVarDeltaAux] = -1;

        colno2[ posVarBinary] = posVarBinary+1;
        row2[ posVarBinary++] = M;

        
        colno[posVarDeltaAux] = posVarDeltaAux + 1;
        row[posVarDeltaAux++] = -1;


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
        
        colno2[ posVarBinary] = posVarBinary+1;
        row2[ posVarBinary++] = M;
        
        colno[posVarDeltaAux] = posVarDeltaAux + 1;
        row[posVarDeltaAux++] = 1;
        


        lp.setColName(posVarBinary, "B" + typeVar + idCity + "b");
        lp.setBinary(posVarBinary, true);
        lp.setColName(posVarDeltaAux, "Z" + typeVar + idCity + "b");
        lp.addConstraintex(Ncol, row2, colno2, LpSolve.GE, 0);
        lp.addConstraintex(Ncol, row, colno, LpSolve.EQ, 0);
        lp.addConstraintex(Ncol, row3, colno3, LpSolve.EQ, 1);*/

    }

    void replace_val_abs(int type) throws LpSolveException {


        /* se crea cada restriccion del tipo (Zxi = Xi  - Xb ) */
        /* normalizado  (Zxi + Xb  =  Xi ) */
        /* si es una varialble Zyi entonces seria  (Zyi + Yb  = Yi ) */
        /* normalizado  (Zyi + Yb  =  Yi ) */

        int[] colno = new int[Ncol];
        double[] row = new double[Ncol];
        colno[j] = j + 1;
        row[j] = 1;
        lp.setUnbounded(j + 1);// para que sea irrestricta
        colno[posDump + type] = posDump + 1 + type;
        row[posDump + type] = 1;
        lp.addConstraintex(Ncol, row, colno, LpSolve.EQ, posCities[j - 2]); //j-2 ya que j inicia en 2

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

            colno[i + posDelta] = 1 + i + posDelta;
            row[i + posDelta] = 1;

            colno[i + posNearbyCity] = 1 + i + posNearbyCity;
            row[i + posNearbyCity] = -1;

            colno[i + posDump] = 1 + i + posDump;
            row[i + posDump] = 1;

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
            row[i + posDelta] = 1;

            colno[i + posBinaryObj] = 1 + i + posBinaryObj;
            row[i + posBinaryObj] = Mobj;

            lp.setBinary(1 + i + posBinaryObj, true);

            colno[i] = 1 + i;
            row[i] = -1;

            lp.addConstraintex(Ncol, row, colno, LpSolve.GE, 0);

            colno = new int[Ncol];
            row = new double[Ncol];

            colno[i + posDelta] = 1 + i + posDelta;
            row[i + posDelta] = 1;

            colno[i + posBinaryObj] = 1 + i + posBinaryObj;
            row[i + posBinaryObj] = Mobj;

            colno[i] = 1 + i;
            row[i] = 1;

            lp.addConstraintex(Ncol, row, colno, LpSolve.LE, Mobj);

            colno = new int[Ncol];
            row = new double[Ncol];

            colno[i + posDelta] = 1 + i + posDelta;
            row[i + posDelta] = 1;

            colno[i] = 1 + i;
            row[i] = -1;

            lp.addConstraintex(Ncol, row, colno, LpSolve.LE, 0);


            row[i + posDelta] = -1;

            lp.addConstraintex(Ncol, row, colno, LpSolve.LE, 0);


        }

        lp.setColName(2 + posBinaryObj, "By0");
        lp.setColName(1 + posBinaryObj, "Bx0");

    }

    void Create_sol() throws LpSolveException {

        //generar modelo en formato lp
        lp.writeLp("modelAndrea.lp");

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

    String Print_sol() throws LpSolveException {

        //lp.printLp();
        //System.out.println("Objective value: " + lp.getObjective());



        double[] row = new double[Ncol];
        lp.getVariables(row);
        //System.out.println("columnas lp " + lp.getNcolumns() + " ncol " + Ncol);

        //System.out.println(lp.getColName(posDump +1) + ": " + row[posDump]);
        //System.out.println(lp.getColName(posDump +2) + ": " + row[posDump +1]);
        //System.out.println(lp.getColName(posNearbyCity +1) + ": " + row[posNearbyCity]);
        //System.out.println(lp.getColName(posNearbyCity +2) + ": " + row[posNearbyCity +1]);


        String sol = "";

        sol += lp.getObjective() + "-"
                + row[posDump] + "-" + row[posDump + 1] + "-"
                + row[posNearbyCity] + "-" + row[posNearbyCity + 1];

        //Para pruebas

        /*for (j = 0; j < Ncol; j++) {
        System.out.println(lp.getColName(j + 1) + ": " + row[j]);
        }*/

        lp.deleteLp();

        return sol;

    }

    //Metodo para elegir la heuristica a usar en el branc&bound
    void setHeuristic(int type, int floor_ceil) {

        /*
         *  public static final int NODE_FIRSTSELECT         = 0; SI
         * 	public static final int NODE_GAPSELECT           = 1; SI
         * 	public static final int NODE_RANGESELECT         = 2; SI
         * 	public static final int NODE_FRACTIONSELECT      = 3; SI
         * 	public static final int NODE_PSEUDOCOSTSELECT    = 4; SI
         * 	public static final int NODE_PSEUDONONINTSELECT  = 5; SI
         *  public static final int NODE_PSEUDORATIOSELECT   = 6; SI         
         * 	public static final int NODE_WEIGHTREVERSEMODE   = 8; SI         
         * 	public static final int NODE_GREEDYMODE         = 32; SI         
         * 	public static final int NODE_DEPTHFIRSTMODE    = 128; SI
         * 	public static final int NODE_RANDOMIZEMODE     = 256; SI         
         * 	public static final int NODE_BREADTHFIRSTMODE = 4096; SI
         *      AUTOORDER SI
         */

        //13 Heuristicas 2 posibilidades de iniciar una rama (ceil = 1 y floor = 2)

        if (floor_ceil == 1) {
            lp.setBbFloorfirst(LpSolve.BRANCH_CEILING);
        } else {
            lp.setBbFloorfirst(LpSolve.BRANCH_FLOOR);
        }


        switch (type) {
            case 1:
                lp.setBbRule(LpSolve.NODE_FIRSTSELECT);
                break;
            case 2:
                lp.setBbRule(LpSolve.NODE_GAPSELECT);
                break;
            case 3:
                lp.setBbRule(LpSolve.NODE_RANGESELECT);
                break;
            case 4:
                lp.setBbRule(LpSolve.NODE_FRACTIONSELECT);
                break;
            case 5:
                lp.setBbRule(LpSolve.NODE_PSEUDOCOSTSELECT);
                break;
            case 6:
                lp.setBbRule(LpSolve.NODE_PSEUDONONINTSELECT);
                break;
            case 7:
                lp.setBbRule(LpSolve.NODE_PSEUDORATIOSELECT);
                break;
            case 8:
                lp.setBbRule(LpSolve.NODE_WEIGHTREVERSEMODE);
                break;
            case 9:
                lp.setBbRule(LpSolve.NODE_GREEDYMODE);
                break;
            case 10:
                lp.setBbRule(LpSolve.NODE_DEPTHFIRSTMODE);
                break;
            case 11:
                lp.setBbRule(LpSolve.NODE_RANDOMIZEMODE);
                break;
            case 12:
                lp.setBbRule(LpSolve.NODE_BREADTHFIRSTMODE);
                break;
            case 13:
                lp.setBbRule(LpSolve.NODE_AUTOORDER);
                break;
        }

    }

    public static void main(String[] args) throws IOException {



        try {

            //Lector de archivos;
            Lector l = new Lector();
            int g[] = {5, 10, 15, 20};
            for (int gr = 0; gr < g.length; gr++) {
                for (int r = 1; r < 3; r++) {

                    String name= "grilla"+g[gr];
                    if(r==1){
                        name+="ceil.csv";
                    }else{
                        name+="floor.csv";
                    }
                    BufferedWriter bw = new BufferedWriter(new FileWriter(name));
                    bw.write("\"h\"-\"t\"-\"nC\"-\"e\"-\"ov\"-\"xb\"-\"yb\"-\"xc\"-\"yc\"\n");

                    for (int i = 0; i < 13; i++) {
                        for (int k = 0; k < 5; k++) {
                            String archivo = "src/Solver/ejemplos_g_e/ejemplo_g";//"/home/andrea/Documentos/UNIVALLE/complejidad_optimizacion/proyecto/EjemplosProyecto/ejemplos_g_e/ejemplo_g";
                            archivo += g[gr] + "-" + k + ".txt";
                            l.leer(archivo);
                            int numCities = l.getNumCiudades();
                            int sizeGrid = l.getTamRegion();
                            int posCities[] = l.getPosCiudades();

                            //System.out.println("sG: " + sizeGrid + " nC: " +  numCities + " e: " + k);


                            //Tiempo para comparar heuristicas:
                            double time_exec;

                            Solver_basuro_andrea obj = new Solver_basuro_andrea(numCities, sizeGrid, posCities);
                            obj.Create_constraints();
                            obj.Create_func_obj();

                            //Pruebas heuristicas:
                            time_exec = System.nanoTime();
                            obj.setHeuristic(i + 1, r);
                            obj.Create_sol();
                            time_exec = System.nanoTime() - time_exec;
                            time_exec /= 1000000;

                            bw.write( /*System.out.println(*/(i + 1) + "-" + time_exec + "-" + numCities + "-" + k + "-" + obj.Print_sol() + "\n");
                            //System.out.println(obj.Print_sol());
                        }

                    }
                    bw.close();

                }
            }



        } catch (LpSolveException e) {
            e.printStackTrace();
        }
        // TODO code application logic here
    }
}
