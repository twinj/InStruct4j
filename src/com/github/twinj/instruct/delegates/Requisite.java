package com.github.twinj.instruct.delegates;

public class Requisite<REQ> {				
		private REQ requisite;
		
		public Requisite(REQ req){
			this.requisite = req;
		}

		public REQ getRequisite() {
			return this.requisite;
		}
	}