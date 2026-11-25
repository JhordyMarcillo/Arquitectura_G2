using System.ComponentModel.DataAnnotations;

namespace ApiPaisesMVC_SQLServer.Models
{
    public class Pais
    {
        public int Id { get; set; }

        [Required(ErrorMessage = "El nombre es obligatorio")]
        [StringLength(100)]
        public string Nombre { get; set; }

        [StringLength(100)]
        public string Capital { get; set; }

        [StringLength(100)]
        public string Region { get; set; }

        [StringLength(100)]
        [Display(Name = "Subregión")]
        public string SubRegion { get; set; }

        [Range(0, long.MaxValue, ErrorMessage = "La población no puede ser negativa")]
        [Display(Name = "Población")]
        public long Poblacion { get; set; }

        [Range(0, double.MaxValue, ErrorMessage = "El área no puede ser negativa")]
        [Display(Name = "Área (km²)")]
        public double Area { get; set; }

        [StringLength(100)]
        public string Moneda { get; set; }
    }
}
