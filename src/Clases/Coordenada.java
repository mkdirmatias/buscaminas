/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

/**
 *
 * @author unkndown
 */
public class Coordenada {
    private int x;
    private int y;
    
    public void setx(int valor)
    {
        x=valor;
    }
    public void sety(int valor)
    {
        y=valor;
    }
    public int getx()
    {
        return x;
    }
    public int gety()
    {
        return y;
    }
    
    public void Coordenada()
    {
        x=0;
        y=0;
    }
}
