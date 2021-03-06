% Scenario Description
-attend_work 		:- excused.
attend_scm 			:- not medical_appointment, ask_for_excuse.
attend_work 		:- not -attend_work.
blacklist 			:- attend_scm.
blacklist			:- not excused, -attend_work.
excused				:- medical_appointment.
medical_appointment	:- not attend_scm, ask_for_excuse.

% The boss thinks who has sensetive information also has a secret
mi_has_secret(D, I) :- mi_sensetive(D, I).