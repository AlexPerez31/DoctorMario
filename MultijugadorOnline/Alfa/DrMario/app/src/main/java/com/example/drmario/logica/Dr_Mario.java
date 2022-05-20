package com.example.drmario.logica;

public class Dr_Mario {

    public int[][] matriz;
    private int FilaActual;
    private int ColumnaActual;
    int ran1 = (1 + (int) (Math.random() * 3)) *10;;
    int ran2 = (1 + (int) (Math.random() * 3)) *10;;
    int posicion = 0;
    public int imagen = 0;
    int siguiente1 = 0;
    int siguiente2 = 0;
    int cont = 0;
    public boolean perdio = false;
    boolean vertical[][] = new boolean[16][8];
    boolean horizontal[][] = new boolean[16][8];
    boolean validarlinea = false;
    public boolean movimiento = true, findeljuego = false;
    public int nivel = 1, numnivel, virus, contarpuntaje = 0, numpuntaje , combo, numcom;
    public long puntaje;





    public Dr_Mario() {
        this.numnivel = nivel - 1;
        this.matriz = new int[16][8];
        int cantidad = 4 * nivel;
        for (int fila = 0; fila < 16; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                this.matriz[fila][columna] = 0;
            }
        }
        for (int i = 0; i < cantidad; i++) {
            generar_virus();
        }
        generar_pastilla();

    }

    public void generar_virus() {
        int fila = 3 + (int) (Math.random() * 13);
        int columna = (int) (Math.random() * 8);
        int color_virus = 1 + (int) (Math.random() * 3);
        color_virus = color_virus * 10;
        if (this.matriz[fila][columna] == 0) {
            this.matriz[fila][columna] = color_virus;
        } else {
            generar_virus();
        }
    }

    public void siguiente_pastilla(){
        siguiente1 = (1 + (int) (Math.random() * 3)) *10;
        siguiente2 = (1 + (int) (Math.random() * 3)) *10;
        if(siguiente1 == 10 && siguiente2 == 10 ){
            imagen = 1;
        } else if(siguiente1 == 10 && siguiente2 == 20 ){
            imagen = 2;
        } else if(siguiente1 == 10 && siguiente2 == 30 ){
            imagen = 3;
        } else if(siguiente1 == 20 && siguiente2 == 10 ){
            imagen = 4;
        } else if(siguiente1 == 20 && siguiente2 == 20 ){
            imagen = 5;
        } else if(siguiente1 == 20 && siguiente2 == 30 ){
            imagen = 6;
        } else if(siguiente1 == 30 && siguiente2 == 10 ){
            imagen = 7;
        } else if(siguiente1 == 30 && siguiente2 == 20 ){
            imagen = 8;
        } else if(siguiente1 == 30 && siguiente2 == 30 ){
            imagen = 9;
        }
        siguiente1 = siguiente1 + 1;
        siguiente2 = siguiente2 + 2;
    }


    public void generar_pastilla() {
        cont ++;
        posicion = 1;
        this.FilaActual = 0;
        this.ColumnaActual = 3;
        this.perdio = perder();
        if(cont<2){
            ran1 = ran1 + 1;
            ran2 = ran2 + 2;
            this.matriz[0][3] = ran1;
            this.matriz[0][4] = ran2;
        }else {
            ran1 = siguiente1;
            ran2 = siguiente2;
            this.matriz[0][3] = ran1;
            this.matriz[0][4] = ran2;
        }
        this.siguiente_pastilla();
    }

    public boolean validarAbajo() {
        if ((this.FilaActual) == 15) {
            return false;
        }else if(posicion % 2 == 0){
            if (this.matriz[this.FilaActual + 1][this.ColumnaActual] != 0) {
                return false;
            }
        }else {
            for (int i = 0; i < 2; i++) {
                if (this.matriz[this.FilaActual + 1][this.ColumnaActual + i] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void limpiarPiezaActual(){
        if(posicion % 2 == 0){
            this.matriz[this.FilaActual][this.ColumnaActual] = 0;
            this.matriz[this.FilaActual - 1][this.ColumnaActual] = 0;
        }else{
            for(int i = 0; i<2; i++ ) {
                this.matriz[this.FilaActual][this.ColumnaActual + i] = 0;
            }
        }
    }

    public void pintarPieza(){
        if(posicion % 2 == 0){
            this.matriz[this.FilaActual][this.ColumnaActual] = this.ran1;
            this.matriz[this.FilaActual-1][this.ColumnaActual] = this.ran2;
        }else {
            this.matriz[this.FilaActual][this.ColumnaActual] = this.ran1;
            this.matriz[this.FilaActual][this.ColumnaActual + 1] = this.ran2;
        }
    }

    //--------------------------------------------------------------------------------------------------------------

    public void abajo(){
        this.contar_virus();
        this.linea_vertical();
        this.linea_horizontal();
        if(validarlinea && !validarAbajo()){
            this.borrar_linea();
            this.contar_puntaje();
            this.transformacion();
            this.caer();
            movimiento = false;
            FilaActual = 15;
        }else if(validarAbajo()) {
            this.limpiarPiezaActual();
            this.FilaActual++;
            this.pintarPieza();
        }else{
            this.numpuntaje = 0;
            this.generar_pastilla();
            movimiento = true;
        }
        if(virus == 0){
            this.ganar();
        }
    }

    //--------------------------------------------------------------------------------------------------------------

    public void izquierda(){
        if(validarIzquierda()) {
            this.limpiarPiezaActual();
            this.ColumnaActual--;
            this.pintarPieza();
        }
    }

    public void derecha(){
        if(validarDerecha()) {
            this.limpiarPiezaActual();
            this.ColumnaActual++;
            this.pintarPieza();
        }
    }

    private boolean validarIzquierda() {
        if(this.ColumnaActual == 0){
            return false;
        }else if(posicion % 2 == 0){
            if(this.matriz[this.FilaActual][this.ColumnaActual - 1] != 0 || this.matriz[this.FilaActual - 1][this.ColumnaActual - 1] != 0){
                return false;
            }
        }else if (this.matriz[this.FilaActual][this.ColumnaActual - 1] != 0) {
                    return false;
        }
        return true;
    }

    private boolean validarDerecha(){
        if(posicion % 2 == 0){
            if((this.ColumnaActual)==7){
                return false;
            }else if(this.matriz[this.FilaActual][this.ColumnaActual + 1] != 0 || this.matriz[this.FilaActual - 1][this.ColumnaActual + 1] != 0){
                return false;
            }
        }else{
            if((this.ColumnaActual+1)==7){
                return false;
            }else if (this.matriz[this.FilaActual][this.ColumnaActual + 2] != 0) {
                return false;
            }
        }
        return true;
    }

    public void rotar(){
        if(this.posicion % 2 != 0){
            if(this.ColumnaActual < 7 && validarRotar()){
                this.ran1 = ran1 + 2;
                this.ran2 = ran2 + 2;
                this.matriz[this.FilaActual][this.ColumnaActual + 1] = 0;
                this.matriz[this.FilaActual][this.ColumnaActual] = ran1;
                this.matriz[this.FilaActual - 1][this.ColumnaActual] = ran2;
                this.posicion++;
            }
        }else{
            if(this.ColumnaActual < 7 && validarRotar()){
                this.ran1 = ran1 - 1;
                this.ran2 = ran2 - 3;
                int aux = ran1;
                ran1 = ran2;
                ran2 = aux;
                this.matriz[this.FilaActual - 1][this.ColumnaActual] = 0;
                this.matriz[this.FilaActual][this.ColumnaActual] = ran1;
                this.matriz[this.FilaActual][this.ColumnaActual + 1] = ran2;
                this.posicion++;
            }else if((this.ColumnaActual == 7 && this.validarRotar()) ){
                this.ran1 = ran1 - 1;
                this.ran2 = ran2 - 3;
                int aux = ran1;
                ran1 = ran2;
                ran2 = aux;
                this.matriz[this.FilaActual - 1][this.ColumnaActual] = 0;
                this.matriz[this.FilaActual][this.ColumnaActual - 1] = ran1;
                this.matriz[this.FilaActual][this.ColumnaActual] = ran2;
                ColumnaActual --;
                this.posicion++;
            }
        }
    }

    public boolean validarRotar(){
        if(this.posicion % 2 != 0){
            if(this.FilaActual == 0){
                return false;
            }else if(this.matriz[this.FilaActual - 1][this.ColumnaActual] != 0){
                return false;
            }
        }else{
            if(this.ColumnaActual < 7 && (this.matriz[this.FilaActual][this.ColumnaActual + 1] != 0)){
                return false;
            }else if(this.ColumnaActual == 7 && this.matriz[this.FilaActual][this.ColumnaActual - 1] != 0){
                return false;
            }
        }
        return true;
    }

    private boolean perder(){
        if(this.matriz[this.FilaActual][this.ColumnaActual] != 0 || this.matriz[this.FilaActual][this.ColumnaActual + 1] != 0 ){
            return true;
        }else {
            return false;
        }
    }

    private void linea_vertical(){
        validarlinea = false;
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 8; j++){
                vertical[i][j] = false;
            }
        }
        int contador, pos, num;
        contarpuntaje = 0;
        this.combo = 0;
        this.numcom = 0;
        for(int columna = 0; columna < 8; columna++){
            pos = 0; contador = 0; num = 0;
            for(int fila = 0; fila < 16; fila++){
                if(this.matriz[fila][columna] >= 10 && this.matriz[fila][columna] < 20 && (fila < 15)) {
                    contador++;
                    if(this.matriz[fila][columna] == 10){
                        contarpuntaje++;
                        combo++;
                    }
                }else if((this.matriz[fila][columna] >= 10 && this.matriz[fila][columna] < 20)  && (fila == 15) && (contador >=3)) {
                    if(this.matriz[fila][columna] == 10){
                        contarpuntaje++;
                        combo++;
                    }
                    contador++;
                    pos = 15;
                    num = contador;
                    numcom = numcom + combo;
                    numpuntaje = numpuntaje + contarpuntaje;
                }else if(contador > 3){
                    pos = fila - 1;
                    num = contador;
                    contador = 0;
                    numcom = numcom + combo;
                    combo = 0;
                    numpuntaje = numpuntaje + contarpuntaje;
                    contarpuntaje = 0;
                }else{
                    contador = 0;
                    combo = 0;
                    contarpuntaje = 0;
                }
            }
            if(num > 3){
                validarlinea = true;
                for(int h = 0; h < num; h++){
                    vertical[pos][columna] = true;
                    pos--;
                }
            }
        }
        for(int columna = 0; columna < 8; columna++){
            pos = 0; contador = 0; num = 0;
            for(int fila = 0; fila < 16; fila++){
                if(this.matriz[fila][columna] >= 20 && this.matriz[fila][columna] < 30 && (fila < 15)) {
                    contador++;
                    if(this.matriz[fila][columna] == 20){
                        contarpuntaje++;
                        combo++;
                    }
                }else if((this.matriz[fila][columna] >= 20 && this.matriz[fila][columna] < 30)  && (fila == 15) && (contador >=3)) {
                    if(this.matriz[fila][columna] == 20){
                        contarpuntaje++;
                        combo++;
                    }
                    contador++;
                    pos = 15;
                    num = contador;
                    numcom = numcom + combo;
                    numpuntaje = numpuntaje + contarpuntaje;
                }else if(contador > 3){
                    pos = fila - 1;
                    num = contador;
                    contador = 0;
                    numcom = numcom + combo;
                    combo = 0;
                    numpuntaje = numpuntaje + contarpuntaje;
                    contarpuntaje = 0;
                }else{
                    contador = 0;
                    combo = 0;
                    contarpuntaje = 0;
                }
            }
            if(num > 3){
                validarlinea = true;
                for(int h = 0; h < num; h++){
                    vertical[pos][columna] = true;
                    pos--;
                }
            }
        }
        for(int columna = 0; columna < 8; columna++){
            pos = 0; contador = 0; num = 0;
            for(int fila = 0; fila < 16; fila++){
                if(this.matriz[fila][columna] >= 30 && (fila < 15)) {
                    contador++;
                    if(this.matriz[fila][columna] == 30){
                        contarpuntaje++;
                        combo++;
                    }
                }else if((this.matriz[fila][columna] >= 30) && (fila == 15) && (contador >=3)) {
                    if(this.matriz[fila][columna] == 30){
                        contarpuntaje++;
                        combo++;
                    }
                    contador++;
                    pos = 15;
                    num = contador;
                    numcom = numcom + combo;
                    numpuntaje = numpuntaje + contarpuntaje;
                }else if(contador > 3){
                    pos = fila - 1;
                    num = contador;
                    contador = 0;
                    numcom = numcom + combo;
                    combo = 0;
                    numpuntaje = numpuntaje + contarpuntaje;
                    contarpuntaje = 0;
                }else{
                    contador = 0;
                    combo = 0;
                    contarpuntaje = 0;
                }
            }
            if(num > 3){
                validarlinea = true;
                for(int h = 0; h < num; h++){
                    vertical[pos][columna] = true;
                    pos--;
                }
            }
        }
    }

    private void linea_horizontal(){
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 8; j++){
                horizontal[i][j] = false;
            }
        }
        int contador, pos, num ;
        for(int fila = 0; fila < 16; fila++){
            pos = 0; contador = 0; num = 0;
            for(int columna = 0; columna < 8; columna++){
                if(this.matriz[fila][columna] >= 10 && this.matriz[fila][columna] < 20 && (columna < 7)) {
                    contador++;
                    if(this.matriz[fila][columna] == 10){
                        contarpuntaje++;
                        combo++;
                    }
                }else if((this.matriz[fila][columna] >= 10 && this.matriz[fila][columna] < 20)  && (columna == 7) && (contador >=3)) {
                    if(this.matriz[fila][columna] == 10){
                        contarpuntaje++;
                        combo++;
                    }
                    contador++;
                    pos = 7;
                    num = contador;
                    numcom = numcom + combo;
                    numpuntaje = numpuntaje + contarpuntaje;
                }else if(contador > 3){
                    pos = columna - 1;
                    num = contador;
                    contador = 0;
                    numcom = numcom + combo;
                    combo = 0;
                    numpuntaje = numpuntaje + contarpuntaje;
                    contarpuntaje = 0;
                }else{
                    contador = 0;
                    combo = 0;
                    contarpuntaje = 0;
                }
            }
            if(num > 3){
                validarlinea = true;
                for(int h = 0; h < num; h++){
                    horizontal[fila][pos] = true;
                    pos--;
                }
            }
        }
        for(int fila = 0; fila < 16; fila++){
            pos = 0; contador = 0; num = 0;
            for(int columna = 0; columna < 8; columna++){
                if(this.matriz[fila][columna] >= 20 && this.matriz[fila][columna] < 30 && (columna < 7)) {
                    contador++;
                    if(this.matriz[fila][columna] == 20){
                        contarpuntaje++;
                        combo++;
                    }
                }else if((this.matriz[fila][columna] >= 20 && this.matriz[fila][columna] < 30)  && (columna == 7) && (contador >=3)) {
                    if(this.matriz[fila][columna] == 20){
                        contarpuntaje++;
                        combo++;
                    }
                    contador++;
                    pos = 7;
                    num = contador;
                    numcom = numcom + combo;
                    numpuntaje = numpuntaje + contarpuntaje;
                }else if(contador > 3){
                    pos = columna - 1;
                    num = contador;
                    contador = 0;
                    numcom = numcom + combo;
                    combo = 0;
                    numpuntaje = numpuntaje + contarpuntaje;
                    contarpuntaje = 0;
                }else{
                    contador = 0;
                    combo = 0;
                    contarpuntaje = 0;
                }
            }
            if(num > 3){
                validarlinea = true;
                for(int h = 0; h < num; h++){
                    horizontal[fila][pos] = true;
                    pos--;
                }
            }
        }
        for(int fila = 0; fila < 16; fila++){
            pos = 0; contador = 0; num = 0;
            for(int columna = 0; columna < 8; columna++){
                if(this.matriz[fila][columna] >= 30 && (columna < 7)) {
                    contador++;
                    if(this.matriz[fila][columna] == 30){
                        contarpuntaje++;
                        combo++;
                    }
                }else if((this.matriz[fila][columna] >= 30)  && (columna == 7) && (contador >=3)) {
                    if(this.matriz[fila][columna] == 30){
                        contarpuntaje++;
                        combo++;
                    }
                    contador++;
                    pos = 7;
                    num = contador;
                    numcom = numcom + combo;
                    numpuntaje = numpuntaje + contarpuntaje;
                }else if(contador > 3){
                    pos = columna - 1;
                    num = contador;
                    contador = 0;
                    numcom = numcom + combo;
                    combo = 0;
                    numpuntaje = numpuntaje + contarpuntaje;
                    contarpuntaje = 0;
                }else{
                    contador = 0;
                    combo = 0;
                    contarpuntaje = 0;
                }
            }
            if(num > 3){
                validarlinea = true;
                for(int h = 0; h < num; h++){
                    horizontal[fila][pos] = true;
                    pos--;
                }
            }
        }
    }

    public void borrar_linea(){
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 8; j++){
                if((horizontal[i][j] == true) || (vertical[i][j] == true)){
                    this.matriz[i][j] = 0;
                }
            }
        }
    }

    public void transformacion(){
        for(int fila = 0; fila < 16; fila++){
            for(int columna = 0; columna < 7; columna++){
                if((this.matriz[fila][columna] == 11) || (this.matriz[fila][columna] == 21) || (this.matriz[fila][columna] == 31)){
                    if(this.matriz[fila][columna + 1] == 0){
                        this.matriz[fila][columna] = matriz[fila][columna] + 4;
                    }
                }
            }
        }
        for(int fila = 0; fila < 16; fila++){
            for(int columna = 1; columna < 8; columna++){
                if((this.matriz[fila][columna] == 12) || (this.matriz[fila][columna] == 22) || (this.matriz[fila][columna] == 32)){
                    if(this.matriz[fila][columna - 1] == 0){
                        this.matriz[fila][columna] = matriz[fila][columna] + 3;
                    }
                }
            }
        }
        for(int fila = 1; fila < 16; fila++){
            for(int columna = 0; columna < 8; columna++){
                if((this.matriz[fila][columna] == 13) || (this.matriz[fila][columna] == 23) || (this.matriz[fila][columna] == 33)){
                    if(this.matriz[fila - 1][columna] == 0){
                        this.matriz[fila][columna] = matriz[fila][columna] + 2;
                    }
                }
            }
        }
        for(int fila = 0; fila < 15; fila++){
            for(int columna = 0; columna < 8; columna++){
                if((this.matriz[fila][columna] == 14) || (this.matriz[fila][columna] == 24) || (this.matriz[fila][columna] == 34)){
                    if(this.matriz[fila + 1][columna] == 0){
                        this.matriz[fila][columna] = matriz[fila][columna] + 1;
                    }
                }
            }
        }
    }

    private void caer(){
        int pos;
        for(int fila = 14; fila >= 0; fila--){
            for(int columna = 0; columna < 8; columna++){
                pos = 0;
                if((this.matriz[fila][columna] == 15) || (this.matriz[fila][columna] == 25) || (this.matriz[fila][columna] == 35)){
                    pos = fila;
                    while((this.matriz[pos + 1][columna] == 0) && (pos < 14)){
                        this.matriz[pos + 1][columna] = this.matriz[pos][columna];
                        this.matriz[pos][columna] = 0;
                        pos++;
                    }
                    if((this.matriz[pos + 1][columna] == 0) && (pos == 14)){
                        this.matriz[pos + 1][columna] = this.matriz[pos][columna];
                        this.matriz[pos][columna] = 0;
                    }
                }else if((this.matriz[fila][columna] == 13) || (this.matriz[fila][columna] == 23) || (this.matriz[fila][columna] == 33)){
                    pos = fila;
                    while((this.matriz[pos + 1][columna] == 0) && (pos < 14)){
                        this.matriz[pos + 1][columna] = this.matriz[pos][columna];
                        this.matriz[pos][columna] = this.matriz[pos - 1][columna];
                        this.matriz[pos - 1][columna] = 0;
                        pos++;
                    }
                    if((this.matriz[pos + 1][columna] == 0) && (pos == 14)){
                        this.matriz[pos + 1][columna] = this.matriz[pos][columna];
                        this.matriz[pos][columna] = this.matriz[pos - 1][columna];
                        this.matriz[pos - 1][columna] = 0;
                    }
                }else if((this.matriz[fila][columna] == 11) || (this.matriz[fila][columna] == 21) || (this.matriz[fila][columna] == 31)){
                    pos = fila;
                    while((this.matriz[pos + 1][columna] == 0) && (this.matriz[pos + 1][columna + 1] == 0) && (pos < 14)){
                        this.matriz[pos + 1][columna] = this.matriz[pos][columna];
                        this.matriz[pos + 1][columna + 1] = this.matriz[pos][columna + 1];
                        this.matriz[pos][columna] = 0;
                        this.matriz[pos][columna + 1] = 0;
                        pos++;
                    }
                    if((this.matriz[pos + 1][columna] == 0) && (this.matriz[pos + 1][columna + 1] == 0) && (pos == 14)){
                        this.matriz[pos + 1][columna] = this.matriz[pos][columna];
                        this.matriz[pos + 1][columna + 1] = this.matriz[pos][columna + 1];
                        this.matriz[pos][columna] = 0;
                        this.matriz[pos][columna + 1] = 0;
                    }
                }
            }
        }
    }

    private void contar_virus(){
        virus = 0;
        for(int fila = 3; fila < 16; fila++){
            for(int columna = 0; columna < 8; columna++){
                if((matriz[fila][columna] == 10) || (matriz[fila][columna] == 20) || (matriz[fila][columna] == 30)){
                    virus++;
                }
            }
        }
    }

    private void contar_puntaje(){
        long contador = 0;
        int contador2 = numpuntaje;
        for(int i = 1; i <= numcom; i ++){
            if(numpuntaje == 1){
                contador = contador + 1;
            }else{
                contador = (long) contador + ((numpuntaje-1) * 2);
            }
             contador2--;
        }
        contador = contador * 100;
        puntaje = puntaje + contador;
    }

    private void ganar(){
        this.nivel++;
        if(nivel < 21){
            this.numnivel = nivel - 1;
            this.matriz = new int[16][8];
            int cantidad = 4 * nivel;
            for (int fila = 0; fila < 16; fila++) {
                for (int columna = 0; columna < 8; columna++) {
                    this.matriz[fila][columna] = 0;
                }
            }
            for (int i = 0; i < cantidad; i++) {
                generar_virus();
            }
            generar_pastilla();
        }else{
            this.findeljuego = true;
        }
    }

}
