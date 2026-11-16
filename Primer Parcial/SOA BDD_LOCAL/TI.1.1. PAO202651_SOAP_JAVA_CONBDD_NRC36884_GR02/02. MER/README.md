# 02. MER

Modelo entidad-relación de EurekaBank (17 tablas), hecho en **PowerDesigner 16.5**. Se abre con *File → Open*.

| Carpeta | Archivo | Contenido |
|---|---|---|
| `01. CONCEPTUAL` | `MODELO_CONCEPTUAL_EUREKABANK.cdm` + `.png` | 16 entidades y sus relaciones. `Permiso` queda como asociación N:N entre `Usuario` y `Modulo` |
| `02. LOGICO` | `MODELO_LOGICO_EUREKABANK.ldm` + `.png` | 17 entidades con sus llaves primarias, foráneas e índices |
| `03. FISICO` | `MODELO_FISICO_EUREKABANK_MYSQL.pdm` + `.png` | 17 tablas y 16 referencias para MySQL |

Cómo se obtuvieron:
1. El **físico** se generó con ingeniería inversa del esquema de `03. BDD/Crea_BD.sql`, tal como queda en MySQL: columnas, tipos, `NULL`, valores por defecto, llaves primarias, únicas, foráneas e índices. Los `CHECK` aparecen como comentario de cada columna (por ejemplo, `Valores: ACTIVO, ANULADO, CANCELADO`).
2. El **lógico** y el **conceptual** se generaron desde el físico con PowerDesigner (*Tools → Generate Logical / Conceptual Data Model*).

`03. FISICO/MODELO_EUREKABANK_MYSQL.pdf` es el diagrama que ya había en el repositorio.
