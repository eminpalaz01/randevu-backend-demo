package com.dershaneproject.randevu.core.utilities.abstracts;

import org.modelmapper.ModelMapper;

public interface ModelMapperService {
	
	ModelMapper forResponse();

	ModelMapper forRequest();
}
