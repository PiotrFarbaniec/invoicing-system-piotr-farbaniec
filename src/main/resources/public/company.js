const loadCompanies = async () => {
    const response = await fetch('http://localhost:7000/companies/get/all');
    const companies = await response.json();

    const companiesTable = document.getElementById("companiesTable")
    companies.forEach(company => {
        const row = companiesTable.insertRow(-1);

        const taxIdentificationCell = row.insertCell(0);
        taxIdentificationCell.innerText = company.taxIdentification;

        const addressCell = row.insertCell(1);
        addressCell.innerText = company.address;

        const nameCell = row.insertCell(2);
        nameCell.innerText = company.name;

        const pensionInsuranceCell = row.insertCell(3);
        pensionInsuranceCell.innerText = company.pensionInsurance;

        const healthInsuranceCell = row.insertCell(4);
        healthInsuranceCell.innerText = company.healthInsurance;
    })
}

const serializeFormToJson = form => JSON.stringify(
    Array.from(new FormData(form).entries())
        .reduce((m, [key, value]) =>
            Object.assign(m, {[key]: value}), {})
);

function handleAddCompanyFormSubmit() {
    const form = $("#addCompanyForm");
    form.on('submit', function (e){
        e.preventDefault();

        $.ajax({
            url: '/companies/add/',
            type: 'post',
            contentType: 'application/json',
            data: serializeFormToJson(this),
            success: function (data) {
//                alert(data)
                loadCompanies()
            },
            error: function (jqXhr, textStatus, errorThrown) {
                alert(errorThrown)
            }
        });
    });
}

window.onload = function () {
    loadCompanies();
    handleAddCompanyFormSubmit()
};