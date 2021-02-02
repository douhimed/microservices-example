let id = 0;

function generateId() {
  return ++id;
}

function random(min, max) {
  return (min + Math.random() * (max - min)).toFixed(2);
}

const enterprises = [
  {
    id: "SG",
    name: "SG",
    seat: "Canada",
    collaboraters: 77000,
    price: random(6000, 8000)
  },
  {
    id: "Atos",
    name: "Atos",
    seat: "France",
    collaboraters: 111000,
    price: random(6000, 8000)
  }
];

function getEnterprises() {
  return enterprises;
}

function getEnterpriseByID(id) {
  return enterprises.find(e => e.id == id);
}

module.exports.getEnterprises = getEnterprises;
module.exports.getEnterpriseByID = getEnterpriseByID;
