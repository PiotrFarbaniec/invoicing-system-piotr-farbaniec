import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Company } from './company'

const PATH = 'companies';

@Injectable({
  providedIn: 'root'
})

export class CompanyService {

  private contentType = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  }

//   private apiUrl(service: string, id: number | null = null): string {
//     const idInUrl = (id !== null ? '/' + id : '');
//     return environment.apiUrl + '/' + service + idInUrl;
//   }

  private apiUrl(service: string, id: number | null = null): string {
    const idInUrl = (id !== null && id !== 0) ? `/${id}` : '';
    return `${environment.apiUrl}/${service}${idInUrl}`;
  }

  constructor(private http: HttpClient) { }

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
    return this.http.get<Company[]>(this.apiUrl(PATH + '/get/all'));
  }

  addCompanies(company: Company): Observable<any> {
    const options = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      responseType: 'text' as 'json'
    };
    return this.http.post<any>(
      this.apiUrl(PATH + '/add/'),
      this.companyRequest(company),
      options
    );
  }

  editingCompany(company: Company): Observable<string> {
    return this.http.put<string>(
      this.apiUrl(PATH + '/update/', company.id),
      this.companyRequest(company),
      { ...this.contentType, responseType: 'text' as 'json' });
  }

  deleteCompany(id: number): Observable<string> {
    return this.http.delete<string>(this.apiUrl(PATH + '/delete/', id),
          { responseType: 'text' as 'json' });
  }

}
