using Microsoft.EntityFrameworkCore;

namespace ApiPaisesMVC_SQLServer.Models
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options)
            : base(options)
        {
        }

        public DbSet<Pais> Paises { get; set; }
        public DbSet<Universidad> Universidades { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // Evita duplicados al importar varias veces
            modelBuilder.Entity<Pais>()
                .HasIndex(p => p.Nombre)
                .IsUnique();

            modelBuilder.Entity<Universidad>()
                .HasIndex(u => new { u.Nombre, u.Pais })
                .IsUnique();
        }
    }
}
