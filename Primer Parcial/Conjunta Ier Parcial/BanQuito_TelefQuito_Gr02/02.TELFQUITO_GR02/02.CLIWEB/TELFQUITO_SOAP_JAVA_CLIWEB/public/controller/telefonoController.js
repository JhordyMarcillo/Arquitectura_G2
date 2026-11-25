const TelefonoService = require("../../services/TelefonoService");

module.exports = (app) => {
  app.post("/getTelefonoById", async (req, res) => {
    const { id } = req.body;

    try {
      const telefono = await telefonoService.getTelefonoById(id);
      res.json({ success: true, telefono });
    } catch (error) {
      console.error("Error fetching telefono:", error);
      res
        .status(500)
        .json({ success: false, message: "Error fetching telefono" });
    }
  });

  app.get("/getAllTelefonos", async (req, res) => {
    try {
      const telefonos = await telefonoService.getAllTelefonos();
      res.json({ success: true, result: telefonos });
    } catch (error) {
      console.error("Error fetching all telefonos:", error);
      res
        .status(500)
        .json({ success: false, message: "Error fetching all telefonos" });
    }
  });
};
