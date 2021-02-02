const express = require("express");
const homeRouter = require("../routes/home");
const enterprisesRouter = require("../routes/enterprises");
const cors = require("cors");

module.exports = function(app) {
  app.use(express.json());
  app.use("/", homeRouter);
  app.use(
    cors({
      origin: [
        "http://localhost:4200",
        "http://localhost:8080",
        "http://localhost:8300"
      ]
    })
  );
  app.use("/enterprises", enterprisesRouter);
};
