import random
import math
import matplotlib.pyplot as plt
#Define player object
class player():
		#Default constructor with skill and name as a paramater with default values of wins and rate
		def __init__(self, skill, name):
				self.skill = skill
				self.unique_skills = [0,0]
				self.name = name
				self.wins = 0
				self.win_rate = 0
				self.current_score = 0
				self.matches_played = 0
				self.game_score = 0
				self.server = False
				
		def recalculate_win_rate(self):
				self.win_rate = float(self.wins)/self.matches_played
				
		def choose_no_to_play_to(self, p2):
				if self.skill < p2.skill:
						return 9
				else:
						return 10
						
		def reset_stats(self):
				self.wins = 0
				self.win_rate = 0
				self.current_score = 0
				self.matches_played = 0
				self.game_score = 0
				
		def set_unique_skills(self, s1, s2):
			self.unique_skills[0] = s1
			self.unique_skills[1] = s2
				
def simulate_game(game_type, p1, p2, unique):
		round_to_play_to = 11
		#Choose random server
		if random.randrange(0,2) == 0:
				p1.server = True
				p2.server= False
		else:
				p1.server = False
				p2.server= True
				
		#Simulate rallies untill there is a winner based on scoring system
		#For game type pars
		if game_type == "pars":
				#While neither player is in a winning suitation
				while not(check_for_winner(p1, p2, "pars", round_to_play_to)):
						#Calculate the winner and loser of each rally
						if unique == False :
							winner, loser = calculate_winner(p1, p2)
						else:
							winner, loser = calculate_winner_q4(p1,p2)
						winner.current_score += 1
						#print winner.name, " : ",winner.current_score
						#print loser.name, " : ",loser.current_score
						#Set the winner to the server
						if winner.server == False:
								winner.server = True;
								loser.server = False;	
		#For game type english
		else:
				first_to_eight = player(50,"")
				round_to_play_to = 9
				while not(check_for_winner(p1, p2, "english", round_to_play_to)):
						if unique == False :
							winner, loser = calculate_winner(p1, p2)
						else:
							winner, loser = calculate_winner_q4(p1,p2)
						if winner.server == True:
								winner.current_score += 1
								if (winner.current_score == 8) and (first_to_eight.name == ""):
										first_to_eight = winner
								else:
										round_to_play_to = first_to_eight.choose_no_to_play_to(loser)										
						else:
								winner.server = True
								loser.server = False
		
		#Check who won and increase statistics
		if(p1.current_score > p2.current_score):
				p1.game_score += 1
		else:
				p2.game_score += 1
				

		#Reset current score and recalculate win rate
		p1.current_score = 0
		p2.current_score = 0
		

		
def simulate_match(game_type, p1, p2, games_to_win):
		bool = True
		while(bool):
			if not(p1.game_score < games_to_win) or not(p2.game_score < games_to_win):
				if(p1.game_score > p2.game_score):
					p1.wins += 1
				else:
					p2.wins +=1
				bool = False
				p1.game_score = 0
				p2.game_score = 0
			else:
				simulate_game(game_type, p1, p2, False)	
		p1.matches_played += 1
		p2.matches_played += 1
		p1.recalculate_win_rate()
		p2.recalculate_win_rate()
		
def simulate_match_q4(game_type, p1, p2, games_to_win):
		bool = True
		while(bool):
			if not(p1.game_score < games_to_win) or not(p2.game_score < games_to_win):
				if(p1.game_score > p2.game_score):
					p1.wins += 1
				else:
					p2.wins +=1
				bool = False
				p1.game_score = 0
				p2.game_score = 0
			else:
				simulate_game(game_type, p1, p2, True)	
		p1.matches_played += 1
		p2.matches_played += 1
		p1.recalculate_win_rate()
		p2.recalculate_win_rate()
		
		
		

def main():
		running = True
		while(running):
			#Create players
			question = raw_input("Which question would you like to find the result to (exit to quit) ? >")
			bob = player(50, "bob")
			susie = player(60, "susie")
			if question == "exit":
					running = False
			elif question == "1a":
					for i in range(0, 1000):
							simulate_match("pars", bob, susie, 1)
					print "Bob win rate : ", bob.win_rate, "\tSusie win rate : ", susie.win_rate
			elif question == "1b":
					#win rate for given number of games
					susie_win_rate = 0.0
					i = 1
					while(susie_win_rate < 0.8):				  
							susie_win_rate = 0
							bob_win_rate, susie_win_rate = calc_win_rate("pars", bob, susie, i)
							i += 2
					print "The lowest value of games in the match that gives susie a greater than 0.8 win rate is  ", i, susie_win_rate
					
			elif question == "1c":
					ratio_to_display = raw_input("What ratio would you like to see the graph for? \n 1. 0.5:1 \n 2. 1:1 \n 3. 2:1 \n >")
					plt.ylabel("Predicted win rate")
					plt.xlabel("Number of games")
					a_win_rates = []
					b_win_rates = []
					for i in range(1,4):
						rob = player(25, "rob")
						josh = player(50, "josh")
						bob = player(50, "bob")
						katie = player(100, "katie")
						#1/2 ratio
						if ratio_to_display == "1":
							for n in range(0,1001):
								simulate_match("pars",rob, josh, i)
							a_win_rates.append(rob.win_rate)
							b_win_rates.append(josh.win_rate)
						#1:1 ratio
						elif ratio_to_display == "2":
							for n in range(0,1001):
								simulate_match("pars",josh, bob, i)
							a_win_rates.append(josh.win_rate)
							b_win_rates.append(bob.win_rate)
						#2:1 ratio
						elif ratio_to_display == "3":
							for n in range(0,1001):
									simulate_match("pars", katie, josh, i)
							a_win_rates.append(katie.win_rate)
							b_win_rates.append(josh.win_rate)
					plt.subplot(211)
					print a_win_rates, b_win_rates
					a_line = plt.plot([1,2,3], a_win_rates,'r--', label="A Win Rates")
					b_line = plt.plot([1,2,3], b_win_rates, label="B Win Rates")
					plt.legend(bbox_to_anchor=(0., 1.02, 1., .102), loc=3,ncol=2, mode="expand", borderaxespad=0.)
					plt.show()
			
			elif question == "2a":
				for i in range(0, 1000):
							simulate_match("english", bob, susie, 1)
				print "Bob win rate : ", bob.win_rate, "\tSusie win rate : ", susie.win_rate
			
			elif question == "2b":
				#win rate for given number of games
					susie_win_rate = 0.0
					i = 1
					while(susie_win_rate < 0.8):				  
							susie_win_rate = 0
							bob_win_rate, susie_win_rate = calc_win_rate("english",bob,susie,i)
							i += 2
					print "The lowest value of games in the match that gives susie a greater than 0.8 win rate is  ", i, susie_win_rate
			
			elif question == "3" :
				print "not currently supported"
			
			elif question == "4":
				good_server_rate = [0,0]
				good_returner_rate = [0, 0]
				bob.set_unique_skills(40, 40) #base player
				susie.set_unique_skills(60, 20) #good server
				rob = player(50, "rob")
				rob.set_unique_skills(20,60) # good returner
				
				
				for i in range(0, 1000):
					simulate_match_q4("pars", bob, susie, 1)
				good_server_rate[0] = bob.win_rate
				bob.reset_stats()
				susie.reset_stats()
				
				for i in range(0, 1000):
					simulate_match_q4("english", bob, susie, 1)
				good_server_rate[1] = bob.win_rate
				bob.reset_stats()
			
				for i in range(0, 1000):
					simulate_match_q4("pars", bob, rob, 1)
				good_returner_rate[0] = bob.win_rate
				bob.reset_stats()
				rob.reset_stats()
				
				for i in range(0, 1000):
					simulate_match_q4("english", bob, rob, 1)
				good_returner_rate[1] = bob.win_rate
				bob.reset_stats()
				
				highest = "english"
				if good_server_rate[0] > good_server_rate[1]:
					highest = "pars"
				print "The winning probability against a good server in english : ", good_server_rate[1], " and pars : ",  good_server_rate[0]
				print "Therefore ", highest, " scoring system is better for an average player"
				
				highest = "english"
				if good_returner_rate[0] > good_returner_rate[1]:
					highest = "pars"
				print "The winning probability against a good server in english : ", good_returner_rate[1], " and pars : ",  good_returner_rate[0]
				print "Therefore ", highest, " scoring system is better for an average player"
				
					
			
			elif question == "testwin":
				for i in range(0, 1000):
					winner, loser = calculate_winner(bob, susie)
					winner.wins += 1
					winner.matches_played += 1
					loser.matches_played +=1
				bob.recalculate_win_rate()
				susie.recalculate_win_rate()
				print bob.win_rate, susie.win_rate
			elif question == "testgame":
				for i in range(0,1001):
					simulate_game("pars", bob, susie)
					bob.matches_played +=1
					susie.matches_played +=1
				bob.wins = bob.game_score
				susie.wins = susie.game_score
				bob.recalculate_win_rate()
				susie.recalculate_win_rate()
				print bob.win_rate, susie.win_rate
			
				
					
					
def calc_win_rate(game_type, p1, p2, no_of_games ):
	p1_prob, p2_prob = calc_player_prob(p1, p2,game_type)
	p1_win_rate = 0.0
	if(no_of_games % 2 == 0):
		for n in range(0, (no_of_games+1)/2, 2):
			p1_win_rate += nCr(no_of_games, n) * (p1_prob **(no_of_games-n)) * (p2_prob **n)
	else:
		for n in range(0, (no_of_games+1)/2):
			p1_win_rate += nCr(no_of_games, n) * (p1_prob **(no_of_games-n)) * (p2_prob **n)
	return p1_win_rate, (1-(p1_win_rate))

		
def calc_player_prob(p1, p2, game_type):		
	for i in range(0, 1000):
		simulate_match(game_type, p1, p2, 1)
	#return probability of p1 or p2 winning a single game  
	p1_prob = p1.win_rate
	p2_prob = p2.win_rate
	p1.reset_stats()
	p2.reset_stats()
	return p1_prob, p2_prob
			

								 

def nCr(n,r):
	f = math.factorial
	return float(f(n) / (f(r) * f(n-r)))
		
def check_for_winner(p1, p2, game_type, round_to_play_to):
		if game_type == "pars":
				if (p1.current_score < 11 and p2.current_score < 11) :
						return False;
				elif math.fabs(p1.current_score - p2.current_score) >= 2:
						return True		 
		else:
				if (p1.current_score <= round_to_play_to and p2.current_score <= round_to_play_to):
						return False;
				else:
						return True;
				
		
		
def calculate_winner_q4(p1, p2):
	random_number = (float(random.randrange(0,101))/100)
	p1_skill = 0
	p2_skill = 0
	if p1.server == True:
		p1_skill = p1.unique_skills[0]
		p2_skill = p2.unique_skills[1]
	else:
		p1_skill = p1.unique_skills[1]
		p2_skill = p2.unique_skills[0]
	probability = (float(p1_skill)/(p1_skill + p2_skill))
	if (random_number <= probability):		
			return p1,p2
	else:
			return p2,p1
	
def calculate_winner(p1, p2):
		
		random_number = (float(random.randrange(0,101))/100)
		probability = (float(p1.skill)/(p1.skill + p2.skill))
		if (random_number <= probability):		
				return p1,p2
		else:
				return p2,p1
				
				
main()
