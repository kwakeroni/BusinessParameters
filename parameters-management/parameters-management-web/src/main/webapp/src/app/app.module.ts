import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule }    from '@angular/http';

import { AppComponent } from './app.component';
import { EntryTableComponent} from './entryTable.component';
import { EntryFormComponent} from './entryForm.component';
import { ParametersService } from './parameters.service';

@NgModule({
  declarations: [
    AppComponent,
    EntryFormComponent,
    EntryTableComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
  ],
  providers: [
    ParametersService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
