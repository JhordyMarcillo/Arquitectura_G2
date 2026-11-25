using System.ComponentModel.DataAnnotations;

namespace ApiPaisesMVC_SQLServer.Models
{
    // Actividad avanzada: universidades obtenidas de otra API pública
    public class Universidad
    {
        public int Id { get; set; }

        [Required]
        [StringLength(200)]
        public string Nombre { get; set; }

        [StringLength(200)]
        public string Dominio { get; set; }

        [StringLength(400)]
        [Display(Name = "Sitio web")]
        public string SitioWeb { get; set; }

        [Required]
        [StringLength(100)]
        [Display(Name = "País")]
        public string Pais { get; set; }
    }
}
