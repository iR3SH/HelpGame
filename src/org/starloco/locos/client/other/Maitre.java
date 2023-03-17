package org.starloco.locos.client.other;

import java.util.ArrayList;

import org.starloco.locos.client.Player;

public class Maitre
{
	public ArrayList<Player> _esclaves = new ArrayList<Player>();
	private Player _maitre = null;
	
	public Maitre(Player maitre)
	{
		_maitre = maitre;
		_esclaves = getpEsclavesInMap();
	}
	
	public void refreh()
	{
		/*for(Player p: new ArrayList<Player>(_esclaves))
		{
			if(!p.isOnline()
			|| p.getCurMap().getId() != _maitre.getCurMap().getId()  )
			{
				_esclaves.remove(p);
			}
		}*/
		if(_esclaves.size() < 1)
			_maitre.set_maitre(null);
	}
	
	
	public void teleportAllEsclaves()
	{
		for(Player p: getEsclaves())
		{
			if(p.getExchangeAction() != null)
				{
					p.sendMessage("Vous n'avez pas pu être téléporté car vous êtes occupé.");
					_maitre.sendMessage("Le héro "+p.getName()+" est occupé et n'a pas pu être téléporté.");
					continue;
				}
			p.teleport(_maitre.getCurMap().getId(), _maitre.getCurCell().getId());
		}
		
	}
	

	public boolean isEsclave(Player perso)
	{
		for(Player p: getEsclaves())
			if(p.getId()==perso.getId())
				return true;
		return false;
	}
	
	public ArrayList<Player> getEsclaves()
	{
		refreh();	
		return new ArrayList<Player>(_esclaves);
	}
	
	private ArrayList<Player> getpEsclavesInMap()
	{
		ArrayList<Player> list = new ArrayList<Player>();
		
		for(Player p: _maitre.getCurMap().getPlayers())
		{
			if(p==null || !p.isOnline())continue;
			if(p.getId() == _maitre.getId())continue;
			if(p.getCurMap().getId() != _maitre.getCurMap().getId()) continue;
			if(p.getFight() != null)continue;
			if(!p.getAccount().getCurrentIp().equals(_maitre.getAccount().getCurrentIp()))continue;
			
			list.add(p);
			//p.sendMessage("Félicitation ! Vous venez de rentrer dans l'escouade de "+_maitre.getName()+".");
			p.setEsclave(true);
		}
		
		return list;
	}
	
	public static boolean hasMaitreInMap(Player perso)
	{
		if(perso.get_maitre()!=null)
			return false;
		
		for(Player p: perso.getCurMap().getPlayers())
			if(p.get_maitre() != null)
				if(p.get_maitre().isEsclave(perso))
					return true;
		return false;
	}
	
}
