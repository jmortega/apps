package com.proyecto.entity;

/**
 * Clase que contiene los datos de una categoria
 */
public class CategoriaEntity {

    private long   _id;
    private String nombre;
    private String descripcion;
    private String icono;

    /**
     * Constructor por defecto
     * 
     */
    public CategoriaEntity() {
        _id=0;
        nombre="";
        descripcion="";
        icono="";
    }
    
    /**
     * Constructor por parametros para un elemento del tipo LugarEntity
     * 
     * @param id
     *            long Identificador unico para la categoria
     * @param nombre
     *            String Nombre de la categoria
     * @param descripcion
     *            String Descripcion de la categoria
     * @param icono
     *            String Uri del icono
     */
    public CategoriaEntity(long id, String nombre, String descripcion, String icono) {
        super();
        this._id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.icono = icono;
    }

    /**
     * Constructor por parametros para un elemento del tipo LugarEntity
     * 
     * @param id
     *            long Identificador unico para el lugar
     * @param nombre
     *            String Nombre del lugar
     */
    public CategoriaEntity(long id, String nombre) {
        super();
        this._id = id;
        this.nombre = nombre;
    }


    /**
     * Constructor por parametros para un elemento del tipo LugarEntity
     * 
     * @param id
     *            long Identificador unico para el lugar
     * @param nombre
     *            String Nombre del lugar
     * @param descripcion
     *            String Descripcion del lugar
     */
    public CategoriaEntity(long id, String nombre, String descripcion) {
        super();
        this._id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public CategoriaEntity(String icono) {
        this.icono = icono;
    }
    
    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }


}
