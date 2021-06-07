import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Information } from './information';
import { MoreInformation } from './more-information';
import { Person } from './person';
import { Remark } from './remark';
import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class DataCollactionService {
  searchName: string;
  user: User = new User()

  constructor(private http: HttpClient) { }

  public getSearchName() {
    return this.searchName;
  }

  public checkUser(user: User): Promise<any> {
    return this.http.post<User>("http://localhost:8080/checkUser", user).toPromise();
  }

  public collectInformation(url: string, location: string): Promise<any> {
    const encoded = encodeURI(url);
    return this.http.get<void>("http://localhost:8080/collectInformation/?location=" + location + "&url=" + encoded).toPromise();
  }

  public checkInformation(url: string): Promise<any> {
    const encoded = encodeURI(url);
    return this.http.get<boolean>("http://localhost:8080/checkInformation/?url=" + encoded).toPromise();
  }

  public getEventList(): Promise<any> {
    return this.http.get<Information>("http://localhost:8080/getEventList").toPromise();
  }

  public getHistoryList(): Promise<any> {
    return this.http.get<History>("http://localhost:8080/getHistoryList").toPromise();
  }

  public getPerson(personName: string): Promise<any> {
    return this.http.get<Person>("http://localhost:8080/getPersonInformation/?personName=" + personName).toPromise();
  }

  public postRemark(remark: Remark, name: string): Promise<any> {
    return this.http.post<Remark>("http://localhost:8080/addRemark/?name=" + name, remark).toPromise();
  }

  public getAllRemarks(name: string): Promise<any> {
    return this.http.get<Remark>("http://localhost:8080/getRemark/?name=" + name).toPromise();
  }

  public getMoreInformation(): Promise<any> {
    return this.http.get<MoreInformation>("http://localhost:8080/getMoreInformation").toPromise();
  }

  public findDataInGlobalDB(location: string): Promise<any> {
    return this.http.get<boolean>("http://localhost:8080/checkDataInGlobal/location=" + location).toPromise();
  }

  public getSearchTime(): Promise<any> {
    return this.http.get<History>("http://localhost:8080/getSearchTimeInGlobal").toPromise();
  }

  public getAllEvent(): Promise<any> {
    return this.http.get<Information>("http://localhost:8080/getAllEvent").toPromise();
  }
}
