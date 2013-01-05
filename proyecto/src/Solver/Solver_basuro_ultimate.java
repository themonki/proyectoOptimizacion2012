/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Solver;

/**
 *
 * @author felipe
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import lpsolve.*;

public class Solver_basuro_ultimate {

    //ciudades ejemplo 1 campus virtual
    //int numCities = 4
    // int sizeGrid = 10
    //int posCities[] = {3, 7, 5, 6, 10, 6, 8, 10};
    // x1 y1
    //ciudades ejemplo 2 campus virtual
    private int numCities;
    private int sizeGrid;
    private int posCities[];
    //Ejemplo Erika
    //int sizeGrid=5, numCities= 4;
    //int posCities[] = {0, 4, 0, 1, 4, 0, 4, 3};
    // Posiciones en la matriz del simplex,
    // ejemplo: posición de la variable Xb en la matriz del simplex
    // Posde Xb = posDump y Pos de Yb = posDump +1
    // posiccion basurero
    private int posDump;
    // posiccion ciudad cercana
    private int posNearbyCity;
    // posicion diferencia entre ciudad cercana y basurero
    private int posDelta;
    // posicion variables binarias utilizadas en la funcion objetivo
    private int posBinaryObj;
    //posicion variables binarias que garantizan que la ciudad cercana es una de las ciudades dadas como entradas
    private int posBinaryCity;
    // posicion variables auxiliares que remplazaran la diferencia entre cada ciudad  y el basurero
    private int posVarDeltaAux;
    //posicion variables binarias que aseguraran que se escoja una  de las ciudades dadas inicialmente
    private int posVarBinary;
    LpSolve lp;
    private int Ncol, j, ret, M, Mobj = 0; //j es usada para indicar la columna de una variable en la matriz simplex

    
    
    //--------Variables para imprimir ///
    private double posXb,posYb,posXc,posYc,funcionObjetivo;

    
    
    public Solver_basuro_ultimate() {
    }

    public void initConstant(int numCities, int sizeGrid, int[] posCities) {

        this.numCities = numCities;
        this.sizeGrid = sizeGrid;
        this.posCities = posCities;




        this.posDump = (numCities * 2) + 2;
        // posiccion ciudad cercana
        this.posNearbyCity = 4 + (numCities * 6);
        // posicion diferencia entre ciudad cercana y basurero
        this.posDelta = 6 + (numCities * 6);
        // posicion variables binarias utilizadas en la funcion objetivo
        this.posBinaryObj = (8 + (numCities * 6));
        //posicion variables binarias que garantizan que la ciudad cercana es una de las ciudades dadas como entradas
        this.posBinaryCity = 10 + (numCities * 6);
        // posicion variables auxiliares que remplazaran la diferencia entre cada ciudad  y el basurero
        this.posVarDeltaAux = 10 + (numCities * 7);
        //posicion variables binarias que aseguraran que se escoja una  de las ciudades dadas inicialmente
        this.posVarBinary = 4 + (numCities * 2);



    }

    public void init() throws LpSolveException {


        initConstraintObvious();

        Create_constraints();

        Create_func_obj();

        Create_sol();

        Print_sol();


    }

    public void initConstraintObvious() throws LpSolveException {

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

        colno2[ posVarDeltaAux] = posVarDeltaAux + 1;
        row2[ posVarDeltaAux] = -1;//Zxia

        colno2[ posVarBinary] = posVarBinary + 1;
        row2[ posVarBinary] = M;//Bxia

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

        colno3[ posVarBinary] = posVarBinary + 1;
        row3[ posVarBinary] = 1;// Bxia 

        posVarBinary++;

        colno3[ posVarBinary] = posVarBinary + 1;
        row3[ posVarBinary] = 1;//Bxib 


        // (  Bxia + Bxib = 1 ) 


        //-------------------------------------------------------------------------------------------------
        lp.setColName(posVarBinary, "B" + typeVar + idCity + "a");// como en la matriz del lp no cuenta desde 0 posVarBinary  se le hizo ++ arriba pa apuntar Bxib pero en el lp apunta Bxia solo indices
        lp.setBinary(posVarBinary, true);

        lp.setColName(posVarDeltaAux, "Z" + typeVar + idCity + "a");// igual que posVarBinary tambien se le hizo ++ arriba  a posVarDeltaAux
        lp.addConstraintex(Ncol, row2, colno2, LpSolve.GE, 0);
        //----------------------------------------------------------------------------------------------------------


        int[] colno4 = new int[Ncol];
        double[] row4 = new double[Ncol];

        colno4[ posVarDeltaAux] = posVarDeltaAux + 1;
        row4[ posVarDeltaAux] = -1;


        colno4[ posVarBinary] = posVarBinary + 1;
        row4[ posVarBinary] = M;

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

        //B&B
        lp.setBbRule(LpSolve.NODE_DEPTHFIRSTMODE);
        lp.setBbFloorfirst(LpSolve.BRANCH_FLOOR);

        //generar modelo en formato lp
        lp.writeLp("modelFelipe.lp");

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

        //lp.printLp();
        System.out.println("Objective value: " + lp.getObjective());

        funcionObjetivo=lp.getObjective();

        
        double[] row = new double[Ncol];
        lp.getVariables(row);
        System.out.println("columnas lp " + lp.getNcolumns() + " ncol " + Ncol);

        for (j = 0; j < Ncol; j++) {
            
            System.out.println(lp.getColName(j + 1) + ": " + row[j]);
            
            if (lp.getColName(j + 1).equals("Xc")){  posXc=row[j];}
            else if (lp.getColName(j + 1).equals("Yc")){ posYc=row[j];}
            else if (lp.getColName(j + 1).equals("Xb")){ posXb=row[j];}
            else if (lp.getColName(j + 1).equals("Yb")){ posYb=row[j];}
            
            
            
        }

        lp.deleteLp();

    }
    
    
    
    public double getPosXb() {
        return posXb;
    }

    public void setPosXb(double posXb) {
        this.posXb = posXb;
    }

    public double getPosYb() {
        return posYb;
    }

    public void setPosYb(double posYb) {
        this.posYb = posYb;
    }

    public double getPosXc() {
        return posXc;
    }

    public void setPosXc(double posXc) {
        this.posXc = posXc;
    }

    public double getPosYc() {
        return posYc;
    }

    public void setPosYc(double posYc) {
        this.posYc = posYc;
    }

    public double getFuncionObjetivo() {
        return funcionObjetivo;
    }

    public void setFuncionObjetivo(double funcionObjetivo) {
        this.funcionObjetivo = funcionObjetivo;
    }

    public static void main(String args[]) {


        try {
            int numCities = 4;
            int sizeGrid = 10;
            int posCities[] = {3, 7, 5, 6, 10, 6, 8, 10};

            Solver_basuro_ultimate solv = new Solver_basuro_ultimate();
            solv.initConstant(numCities, sizeGrid, posCities);
            solv.init();
            
            System.out.println( "objetivo "+solv.getFuncionObjetivo()+"  Xb "+ solv.getPosXb()+" Yb "+solv.getPosYb()+" Xc "+solv.getPosXc()+"Yc"+solv.getPosYc());
        } catch (LpSolveException e) {
            e.printStackTrace();
        }



    }
}
