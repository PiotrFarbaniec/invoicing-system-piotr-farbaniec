import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Company } from './company'

const PATH = 'companies';

@Injectable({
  providedIn: 'root',
})

export class CompanyService {
  constructor(private http: HttpClient) { }

  private contentType = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  };

  private apiUrl(service: string, id: number = 0): string {
    const idInUrl = id !== 0 ? '/' + id : '';

    return environment.apiUrl + '/' + service + idInUrl;
  }

  private companyRequest(company: Company) {
    return {
      taxIdentification: company.taxIdentification,
      address: company.address,
      name: company.name,
      pensionInsurance: company.pensionInsurance,
      healthInsurance: company.healthInsurance
    };
  }

  getCompanies(): Observable<Company[]> {
    return this.http.get<Company[]>(this.apiUrl(PATH));
  }

  addCompany(company: Company): Observable<any> {
    return this.http.post<any>(
      this.apiUrl(PATH),
      this.companyRequest(company),
      this.contentType
    );
  }

  editingCompany(company: Company): Observable<any> {
    return this.http.put<Company>(
      this.apiUrl(PATH, company.id),
      this.companyRequest(company),
      this.contentType
    );
  }

  deleteCompany(id: number): Observable<any> {
    return this.http.delete<any>(this.apiUrl(PATH, id));
  }
}


