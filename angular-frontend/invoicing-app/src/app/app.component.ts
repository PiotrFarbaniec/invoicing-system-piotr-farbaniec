import { Component, OnInit } from '@angular/core';
import { Company } from './company';
import { CompanyService } from './company.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit {
  companies: Company[] = [];
  newCompany: Company = new Company(0, "", "", "", 0, 0);

  constructor(private companyService: CompanyService) { }

  ngOnInit(): void {
    this.companyService.getCompanies().subscribe((companies) => {
    this.companies = companies;
    });
  }

  addCompany() {
    this.companyService.addCompanies(this.newCompany).subscribe((id) => {
      this.newCompany.id = id;
      this.companies.push(this.newCompany);
      this.newCompany = new Company(0, "", "", "", 0, 0);
    });
  }

  deleteCompany(companyToDelete: Company) {
    this.companyService.deleteCompany(companyToDelete.id)
      .subscribe(() => { this.companies = this.companies.filter(
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
    if(updatedCompany.editedCompany !== null) {
      this.companyService.editingCompany(updatedCompany.editedCompany).subscribe(() => {
        updatedCompany.taxIdentification = updatedCompany.editedCompany.taxIdentification;
        updatedCompany.address = updatedCompany.editedCompany.address;
        updatedCompany.name = updatedCompany.editedCompany.name;
        updatedCompany.pensionInsurance = updatedCompany.editedCompany.pensionInsurance;
        updatedCompany.healthInsurance = updatedCompany.editedCompany.healthInsurance;
        updatedCompany.editMode = false;
      });
    }
  }

}
