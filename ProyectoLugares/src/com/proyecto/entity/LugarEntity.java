package com.proyecto.entity;

/**
 * Clase que contiene los datos del lugar
 */
public class LugarEntity {

    private long   _id;
    private String nombre;
    private String descripcion;
    private double latitud;
    private double longitud;
    private String foto;
    private Float  rating;
    private long   categoria;

    /**
     * Constructor por defecto
     * 
     */
    public LugarEntity() {
        _id=0;
        nombre="";
        descripcion="";
        latitud=0;
        longitud=0;
        foto="";
        rating=new Float(0);
        categoria=0;
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
     * @param foto
     *            String Uri de la foto
     */
    public LugarEntity(long id, String nombre, String descripcion, String foto) {
        super();
        this._id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.foto = foto;
    }
    
    public LugarEntity(long id, String nombre, String descripcion,String foto, long categoria) {
        super();
        this._id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.foto = foto;
        this.categoria = categoria;
    }

    /**
     * Constructor por parametros para un elemento del tipo LugarEntity
     * 
     * @param id
     *            long Identificador unico para el lugar
     * @param nombre
     *            String Nombre del lugar
     * @param latitud
     *            double Latitud del lugar
     * @param longitud
     *            double Longitud del lugar
     */
    public LugarEntity(long id, String nombre, double latitud, double longitud) {
        super();
        this._id = id;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    /**
     * Constructor por parámetros para un elemento del tipo LugarEntity
     * 
     * @param id
     *            long Identificador unico para el lugar
     * @param nombre
     *            String Nombre del lugar
     * @param descripcion
     *            String Descripcion del lugar
     * @param latitud
     *            double Latitud del lugar
     * @param longitud
     *            double Longitud del lugar
     * @param foto
     *            String Uri de la foto
     */
    public LugarEntity(long id, String nombre, String descripcion, double latitud, double longitud, String foto) {
        super();
        this._id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.foto = foto;
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
     * @param latitud
     *            double Latitud del lugar
     * @param longitud
     *            double Longitud del lugar
     */
    
    public LugarEntity(long id, String nombre, String descripcion, double latitud, double longitud) {
        super();
        this._id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
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
    public LugarEntity(long id, String nombre, String descripcion) {
        super();
        this._id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public LugarEntity(String foto) {
        this.foto = foto;
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

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
    
    public long getCategoria() {
        return categoria;
    }

    public void setCategoria(long categoria) {
        this.categoria = categoria;
    }

}
