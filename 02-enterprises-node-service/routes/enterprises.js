const express = require("express");
const router = express();
const EnterpriseServices = require("../services/enterpriseService");

router.get("/", async (req, resp) => {
  const enterprises = await EnterpriseServices.getEnterprises();
  return resp.status(200).send(enterprises);
});

router.get("/:id", async (req, resp) => {
  const enterprise = await EnterpriseServices.getEnterpriseByID(req.params.id);
  if (!enterprise)
    return resp
      .status(404)
      .send("The enterprise with the given ID was not found.");
  resp.status(200).send(enterprise);
});

module.exports = router;
