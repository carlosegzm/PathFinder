-- -----------------------------------------------
-- CAPITALS (coordinates — adjust manually if needed)
-- -----------------------------------------------
INSERT INTO capitals (id, name, latitude, longitude) VALUES
('AC', 'Rio Branco',       -9.97400000,  -67.80760000),
('AL', 'Maceió',           -9.66625000,  -35.73510000),
('AP', 'Macapá',            0.03445660,  -51.06660000),
('AM', 'Manaus',           -3.10719000,  -60.02610000),
('BA', 'Salvador',        -12.97040000,  -38.51240000),
('CE', 'Fortaleza',        -3.71839000,  -38.54340000),
('DF', 'Brasília',        -15.78010000,  -47.92920000),
('ES', 'Vitória',         -20.32220000,  -40.33840000),
('GO', 'Goiânia',         -16.67990000,  -49.25500000),
('MA', 'São Luís',         -2.53073000,  -44.30680000),
('MT', 'Cuiabá',          -15.59890000,  -56.09490000),
('MS', 'Campo Grande',    -20.44350000,  -54.64780000),
('MG', 'Belo Horizonte',  -19.81570000,  -43.95420000),
('PA', 'Belém',            -1.45502000,  -48.50240000),
('PB', 'João Pessoa',      -7.11532000,  -34.86100000),
('PR', 'Curitiba',        -25.42840000,  -49.27330000),
('PE', 'Recife',           -8.05428000,  -34.88130000),
('PI', 'Teresina',         -5.08921000,  -42.80160000),
('RJ', 'Rio de Janeiro',  -22.90350000,  -43.20960000),
('RN', 'Natal',            -5.79448000,  -35.21100000),
('RS', 'Porto Alegre',    -30.02770000,  -51.22870000),
('RO', 'Porto Velho',      -8.76183000,  -63.90200000),
('RR', 'Boa Vista',         2.81954000,  -60.67140000),
('SC', 'Florianópolis',   -27.59690000,  -48.54950000),
('SP', 'São Paulo',       -23.54890000,  -46.63880000),
('SE', 'Aracaju',         -10.90950000,  -37.07480000),
('TO', 'Palmas',          -10.16890000,  -48.33170000);

-- -----------------------------------------------
-- PATHS BETWEEN CAPITALS — Google Maps distances (km)
-- All routes are bidirectional. has_railway = FALSE by default.
-- -----------------------------------------------
-- AC - Rio Branco
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('AC', 'AM',  1395), ('AM', 'AC',  1395); -- BR-364 + AM-010
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('AC', 'RO',   509), ('RO', 'AC',   509); -- BR-364

-- AL - Maceió
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('AL', 'PE',   256), ('PE', 'AL',   256); -- BR-101
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('AL', 'SE',   272), ('SE', 'AL',   272); -- BR-101
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('AL', 'BA',   579), ('BA', 'AL',   579); -- BR-101

-- AP - Macapá
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('AP', 'PA',   527), ('PA', 'AP',   527); -- BR-156 + river crossing

-- AM - Manaus
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('AM', 'RO',   889), ('RO', 'AM',   889); -- BR-319 + BR-364
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('AM', 'MT',  2349), ('MT', 'AM',  2349); -- BR-319 + BR-364
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('AM', 'PA',  3048), ('PA', 'AM',  3048); -- AM-010 + BR-010
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('AM', 'RR',   747), ('RR', 'AM',   747); -- BR-174

-- BA - Salvador
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('BA', 'SE',   323), ('SE', 'BA',   323); -- BR-101
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('BA', 'PE',   806), ('PE', 'BA',   806); -- BR-101
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('BA', 'PI',  1152), ('PI', 'BA',  1152); -- BR-324 + BR-135
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('BA', 'TO',  1474), ('TO', 'BA',  1474); -- BR-242 + BR-010
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('BA', 'GO',  1646), ('GO', 'BA',  1646); -- BR-242 + BR-060
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('BA', 'MG',  1434), ('MG', 'BA',  1434); -- BR-116
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('BA', 'ES',  1175), ('ES', 'BA',  1175); -- BR-101

-- CE - Fortaleza
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('CE', 'PI',   603), ('PI', 'CE',   603); -- BR-222 + BR-343
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('CE', 'RN',   524), ('RN', 'CE',   524); -- BR-304
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('CE', 'PB',   673), ('PB', 'CE',   673); -- BR-116 + BR-230
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('CE', 'PE',   779), ('PE', 'CE',   779); -- BR-116

-- DF - Brasília  [mandatory exception per assignment: GO and MG]
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('DF', 'GO',   207), ('GO', 'DF',   207); -- BR-060
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('DF', 'MG',   739), ('MG', 'DF',   739); -- BR-040

-- ES - Vitória
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('ES', 'MG',   515), ('MG', 'ES',   515); -- BR-262
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('ES', 'RJ',   518), ('RJ', 'ES',   518); -- BR-101

-- GO - Goiânia
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('GO', 'TO',   824), ('TO', 'GO',   824); -- BR-153
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('GO', 'MG',   891), ('MG', 'GO',   891); -- BR-040
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('GO', 'MS',   840), ('MS', 'GO',   840); -- BR-060
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('GO', 'MT',   887), ('MT', 'GO',   887); -- BR-364

-- MA - São Luís
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('MA', 'PA',   576), ('PA', 'MA',   576); -- BR-316
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('MA', 'TO',  1249), ('TO', 'MA',  1249); -- BR-010
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('MA', 'PI',   436), ('PI', 'MA',   436); -- BR-316 + BR-343

-- MT - Cuiabá
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('MT', 'RO',  1461), ('RO', 'MT',  1461); -- BR-364
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('MT', 'PA',  2629), ('PA', 'MT',  2629); -- BR-163
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('MT', 'TO',  1487), ('TO', 'MT',  1487); -- BR-158
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('MT', 'MS',   707), ('MS', 'MT',   707); -- BR-163

-- MS - Campo Grande
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('MS', 'MG',  1262), ('MG', 'MS',  1262); -- BR-262 + BR-040
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('MS', 'SP',   987), ('SP', 'MS',   987); -- BR-262
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('MS', 'PR',   979), ('PR', 'MS',   979); -- BR-163 + BR-277

-- MG - Belo Horizonte
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('MG', 'RJ',   441), ('RJ', 'MG',   441); -- BR-040
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('MG', 'SP',   583), ('SP', 'MG',   583); -- BR-381 Fernão Dias

-- PA - Belém
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('PA', 'TO',  1207), ('TO', 'PA',  1207); -- BR-010
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('PA', 'RR',  2808), ('RR', 'PA',  2808); -- BR-010 + BR-174

-- PB - João Pessoa
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('PB', 'RN',   181), ('RN', 'PB',   181); -- BR-101
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('PB', 'PE',   116), ('PE', 'PB',   116); -- BR-101

-- PR - Curitiba
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('PR', 'SP',   424), ('SP', 'PR',   424); -- BR-116 Régis Bittencourt
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('PR', 'SC',   307), ('SC', 'PR',   307); -- BR-101

-- PE - Recife
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('PE', 'PI',  1127), ('PI', 'PE',  1127); -- BR-232 + BR-316

-- PI - Teresina
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('PI', 'TO',  1107), ('TO', 'PI',  1107); -- BR-010 + BR-226

-- RJ - Rio de Janeiro
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('RJ', 'SP',   435), ('SP', 'RJ',   435); -- BR-116 Via Dutra

-- RS - Porto Alegre
INSERT INTO path_between_capitals (origin_id, destination_id, distance) VALUES ('RS', 'SC',   463), ('SC', 'RS',   463); -- BR-101

-- -----------------------------------------------
-- COMMON ROUTES — Annex 1 (daily freight demand)
-- -----------------------------------------------
INSERT INTO common_routes (origin_id, destination_id, load) VALUES
('SP', 'RJ', 150),
('DF', 'GO', 140),
('RJ', 'MG', 130),
('SP', 'PE', 120),
('AM', 'SP', 110),
('CE', 'SP', 100),
('RS', 'DF',  90),
('SP', 'BA',  90),
('RJ', 'BA',  85),
('PA', 'AM',  80),
('MG', 'DF',  80),
('PA', 'RJ',  75),
('SC', 'RS',  75),
('PE', 'BA',  75),
('PR', 'RN',  70),
('PR', 'SC',  70),
('RS', 'RJ',  70),
('MA', 'PA',  65),
('SP', 'SC',  65),
('BA', 'DF',  65),
('MG', 'MA',  60),
('RN', 'CE',  60),
('MT', 'GO',  60),
('MT', 'ES',  55),
('CE', 'PI',  55),
('ES', 'RJ',  55),
('MS', 'PR',  55),
('DF', 'MT',  55),
('MS', 'PE',  50),
('PE', 'PB',  50),
('PI', 'MA',  50),
('AM', 'RO',  50),
('RJ', 'ES',  50),
('AL', 'PE',  45),
('PB', 'RN',  45),
('RO', 'MT',  45),
('PI', 'DF',  45),
('BA', 'SE',  40),
('AM', 'RR',  40),
('TO', 'DF',  40),
('PB', 'BA',  40),
('SE', 'AL',  35),
('RO', 'AC',  35),
('TO', 'PA',  35),
('AL', 'MG',  35),
('PA', 'AP',  30),
('AP', 'CE',  30),
('SE', 'RJ',  30),
('AC', 'SP',  25),
('RR', 'DF',  20);