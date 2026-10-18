package umg.edu.gt.progra2.modelo

enum class Categoria(val nombreVisible: String, val tipo: Tipo) {
    //Gastos
    ALIMENTACION("Alimentación", Tipo.GASTO ),
    TRANSPORTE("Transporte", Tipo.GASTO ),
    ESTUDIOS("Estudios", Tipo.GASTO ),
    ENTRETENIMIENTO("Entretenimiento", Tipo.GASTO ),
    SALUD("Salud", Tipo.GASTO ),
    OTROS_GASTOS("Otros gastos", Tipo.GASTO ),

    //Ingresos
    SUELDO("Sueldo", Tipo.INGRESO),
    MESADA("Mesada", Tipo.INGRESO),
    TRABAJOS_EXTRA("Trabajos extras", Tipo.INGRESO),
    BECA("Beca", Tipo.INGRESO),
    VENTAS("Venatas", Tipo.INGRESO),
    OTROS_INGRESOS("Otros ingresos", Tipo.INGRESO),;



    override fun toString(): String = nombreVisible

}