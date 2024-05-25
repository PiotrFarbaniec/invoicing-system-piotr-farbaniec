import { Component } from '@angular/core';
import { Company } from "./company";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {

  newCompany: Company = new Company("", "", "", 0, 0);

  addCompany() {
  this.companies.push(this.newCompany);
  this.newCompany = new Company("", "", "", 0, 0);
  }

  companies: Company[] = [
    new Company(
      "308-423-11-11",
      "City 1, Example street 11",
      "EXAMPLE COMPANY 1",
      437.49,
      379.81
    ),
    new Company(
      "516-433-22-22",
      "City 2, Example street 22",
      "EXAMPLE COMPANY 2",
      647.77,
      568.21
    ),
    new Company(
      "840-577-33-33",
      "City 3, Example street 33",
      "EXAMPLE COMPANY 3",
      839.49,
      767.41
    )
  ]
}
