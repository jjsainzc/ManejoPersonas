 Ejemplo Manejo de personas
 <p>
 Curso Android (Modulo 1 y 2)

 @author Jorge Jesus Sainz Casalla
 Instructor de lenguajes de programacion

 NOTA: Solo hay uso de ñ en los comentarios, no hay uso de acentos.
 <p>
 CEC (Enero-Marzo 2018)
 <pre>
 <p>
  Temas que abarca.
 -------------------------------------------------------------------------------------------------
 - Diseño general, layouts, uso de componentes, recursos.
 - Patrones de diseños basados en estaticos.
 - Navegacion lateral y uso de boton flotante.
 - Menus superiores y contextuales.
 - Uso de Snackbar para recuperar la accion.
 - Estilos de texto usando Spannable y Html
 - Eventos con declaraciones en clase y en componentes
 - Preferencias publicas del usuario, usando fragmento.
 - Dialogos de seleccion simple y de tipo alerta.
 - Entrada/Salida, flujos de bytes y caracteres
 - Ordenamiento dinamicos de lista usando Comparator
 - Componente y tratamiento de fecha.
 - ListView y Spinner, adaptadores y optimizaciones.
 - Lanzamiento de Activities, simples y en espera de un resultado.
 - Parcelables.
 - Validacion de datos de un formulario usando expresiones regulares y
   base de datos para evitar repeticiones de datos en campos unicos
 - Uso de fragmentos dinamicos y callbacks, paso de parametros por arguments.
 - Uso de XML y Json para serializar objetos y guardar archivo.
 - Conversores personalizados para Gson y Xstream
 - Uso de base de datos.
 - Uso de anotaciones y reflexion para construir coleccion de atributos y valores
 - Creacion de proceso en segundo plano usando una clase que hereda de AsyncTask
 - Uso de metodo estatico en clase utilitaria para hacer conexion universal a RESTfull y
   Servlet, asi como tambien para scripts de PHP siempre y cuando devuelvan un codigo
   HTML correcto.
 - Formateo de XML (utilidad).
 - Guardado de un archivo XML que representa el listado en el DOWNLOADS con el nombre
 personas.xml
 - Lectura de un webservice tipo SOAP
 
 NOTAS: 
 - Cuando se usa ksoap2-android-assembly-2.4-jar-with-dependencies.jar, no se puede
   usar xmlpull-1.1.3.1.jar de XStream porque entran en conflicto de multiDex, entonces
   dejar solo la biblioteca de ksoap
 - Trabajo con mapas, lectura, adicion de marcas, cambios de visualizacion.
 - Proveedor de contenido, definicion y creacion de acciones.
 - El uso del patron de dise&ntilde;o Singleton, no se puede aplicar en los fragmentos,
   los metodos newInstance que estan creados en cada uno, solo facilitan el manejo del mismo.
 -----------------------------------------------------------------------------------------------
 <p>
 Funcionalidad general
 <p>
 A partir de un objeto Persona (POJO) realizar un mantenimiento de datos usando una pantalla de
 fragment_listado (Listado) que tiene la responsabilidad de adicionar, modificar, borrar y ordenar
 los mismos, una segunda pantalla (Formulario) es la que realiza las acciones  de adicionar y 
 modificacion.
 <p>
 La pantalla de Listado puede cambiar el ordenamiento mediante un menu superior.
 <p>
 El menu lateral brinda la posibilidad de establecer preferencias, guardarArchivoObjeto manualmente
 la lista, mostrar los objetos serializados en json y leer de webservices tipo SOAP y RESTfull
 <p>
 Para mantener el menu lateral se usan fragmentos que son manejados en un FrameLayout en el
 layout principal.
 <p>
 El almacenamiento de la lista se hace en el directorio privado de la aplicacion usando la modalidad
 binaria que corresponde a la jerarquia de flujo de bytes y en el directorio DOWNLOADS usando la
 jerarquia  de flujo de caracteres (XML).
 <p>
 La clase principal MainActivity implementa la interface OnFragmentListado del fragmento Listado.
 Serializa los objetos con Json para mostrarlos en una pantalla aparte.
 Lee una lista XML de webservices tipo SOAP y RESTfull y la muestra en una pantalla aparte.
 <p>
 Inclusion de un mapa en fragmento, adicion de marcas y cambios de visualizacion.
 <p>
 Creacion de un proveedor de contenidos que permita la recuperacion e insercion de datos.
</pre>
