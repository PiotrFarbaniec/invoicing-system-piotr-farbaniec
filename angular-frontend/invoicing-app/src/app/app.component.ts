import { Component } from '@angular/core';
import { Company } from './company';
import { CompanyService } from './company.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  companies: Company[] = [];
  newCompany: Company = new Company(0, "", "", "", 0, 0);

  constructor(private companyService: CompanyService) { }

  ngOnInit(): void {
    this.companyService.getCompanies().subscribe((companies) => {
    this.companies = companies;
    });
  }

  addCompany() {
    this.companyService.addCompany(this.newCompany).subscribe((id) => {
      this.newCompany.id = id;
      this.companies.push(this.newCompany);
      this.newCompany = new Company(0, "", "", "", 0, 0);
    });
  }

  deleteCompany(companyToDelete: Company) {
    this.companyService.deleteCompany(companyToDelete.id).subscribe(
    () => { this.companies = this.companies.filter(
            (company) => company !== companyToDelete);
    });
  }

  triggerUpdate(company: Company) {
    company.editedCompany = new Company(
      company.id,
      company.taxIdentification,
      company.address,
      company.name,
      company.pensionInsurance,
      company.healthInsurance
    );
    company.editMode = true;
  }

  cancelCompanyUpdate(company: Company) {
    company.editMode = false;
  }

  updateCompany(updatedCompany: Company) {
    this.companyService.editingCompany(updatedCompany.editedCompany).subscribe(() => {
      updatedCompany.taxIdentification = updatedCompany.editedCompany?.taxIdentification
      updatedCompany.address = updatedCompany.editedCompany?.address
      updatedCompany.name = updatedCompany.editedCompany?.name
      updatedCompany.pensionInsurance = updatedCompany.editedCompany?.pensionInsurance
      updatedCompany.healthInsurance = updatedCompany.editedCompany?.healthInsurance
      updatedCompany.editMode = false;
    });
  }


  /* companies: Company[] = [
    new Company(
      1,
      "308-423-11-11",
      "City 1, Example street 11",
      "EXAMPLE COMPANY 1",
      437.49,
      379.81
    ),
    new Company(
      2,
      "516-433-22-22",
      "City 2, Example street 22",
      "EXAMPLE COMPANY 2",
      647.77,
      568.21
    ),
    new Company(
      3,
      "840-577-33-33",
      "City 3, Example street 33",
      "EXAMPLE COMPANY 3",
      839.49,
      767.41
    )
  ] */

  /* addCompany() {
    this.companies.push(this.newCompany);
    this.newCompany = new Company("", "", "", 0, 0);
  } */

/*   deleteCompany(companyToDelete: Company) {
    this.companies = this.companies.filter(company => company !== companyToDelete);
  } */



  /* updateCompany(updatedCompany: Company) {
    updatedCompany.taxIdentification = updatedCompany.editedCompany.taxIdentification
    updatedCompany.address = updatedCompany.editedCompany.address
    updatedCompany.name = updatedCompany.editedCompany.name
    updatedCompany.pensionInsurance = updatedCompany.editedCompany.pensionInsurance
    updatedCompany.healthInsurance = updatedCompany.editedCompany.healthInsurance

    updatedCompany.editMode = false;
  } */

}
