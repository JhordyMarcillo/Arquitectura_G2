namespace ec.edu.monster.controladores.manejadores
{
    /// <summary>Marca las acciones que no exigen token (solo el login).</summary>
    [AttributeUsage(AttributeTargets.Method)]
    public class PermitirAnonimo : Attribute
    {
    }
}
